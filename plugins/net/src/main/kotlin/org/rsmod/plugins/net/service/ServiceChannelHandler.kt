package org.rsmod.plugins.net.service

import com.github.michaelbull.logging.InlineLogger
import com.google.common.hash.Hashing
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.openrs2.crypto.IsaacRandom
import org.openrs2.crypto.secureRandom
import org.rsmod.game.client.Client
import org.rsmod.game.client.ClientList
import org.rsmod.game.events.EventBus
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.game.store.player.PlayerCodec
import org.rsmod.game.store.player.PlayerDataRequest
import org.rsmod.game.store.player.PlayerDataResponse
import org.rsmod.plugins.api.event.ClientSession
import org.rsmod.plugins.net.game.client.Platform
import org.rsmod.plugins.net.game.clientAttr
import org.rsmod.plugins.net.game.setClientAttr
import org.rsmod.plugins.net.js5.Js5ChannelHandler
import org.rsmod.plugins.net.js5.downstream.Js5GroupResponseEncoder
import org.rsmod.plugins.net.js5.downstream.Js5RemoteDownstream
import org.rsmod.plugins.net.js5.downstream.Js5Response
import org.rsmod.plugins.net.js5.downstream.XorDecoder
import org.rsmod.plugins.net.js5.upstream.Js5RequestDecoder
import org.rsmod.plugins.net.login.downstream.LoginDownstream
import org.rsmod.plugins.net.login.downstream.LoginResponse
import org.rsmod.plugins.net.login.upstream.LoginPacketRequest
import org.rsmod.plugins.net.rev.Revision
import org.rsmod.plugins.net.rev.platform.GamePlatformPacketMaps
import org.rsmod.plugins.net.service.downstream.ServiceResponse
import org.rsmod.plugins.net.service.upstream.ServiceRequest
import org.rsmod.protocol.Protocol
import org.rsmod.protocol.ProtocolDecoder
import org.rsmod.protocol.ProtocolEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale
import javax.inject.Inject
import javax.inject.Provider

private val logger = InlineLogger()

class ServiceChannelHandler @Inject constructor(
    @Js5RemoteDownstream private val js5RemoteDownstream: Protocol,
    @LoginDownstream private val loginDownstream: Protocol,
    private val playerCodec: PlayerCodec,
    private val js5HandlerProvider: Provider<Js5ChannelHandler>,
    private val gamePackets: GamePlatformPacketMaps,
    private val events: EventBus,
    private val players: PlayerList,
    private val clients: ClientList
) : SimpleChannelInboundHandler<ServiceRequest>(ServiceRequest::class.java) {

    private lateinit var scope: CoroutineScope
    private var serverKey = 0L

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        val exceptionHandler = CoroutineExceptionHandler { _, ex -> ctx.fireExceptionCaught(ex) }
        scope = CoroutineScope(ctx.executor().asCoroutineDispatcher() + exceptionHandler)
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        scope.cancel()
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.read()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        // TODO: offload client unregister to a game-thread-safe dispatcher
        ctx.channel().clientAttr()?.let { client ->
            clients -= client
            events += ClientSession.Disconnect(client)
        }
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ServiceRequest) {
        when (msg) {
            ServiceRequest.InitGameConnection -> handleInitGameConnection(ctx)
            is ServiceRequest.InitJs5RemoteConnection -> handleInitJs5RemoteConnection(ctx, msg)
            is ServiceRequest.GameLogin -> handleGameLogin(ctx, msg)
        }
    }

    private fun handleInitGameConnection(ctx: ChannelHandlerContext) {
        serverKey = secureRandom.nextLong()
        ctx.write(ServiceResponse.ExchangeSessionKey(serverKey), ctx.voidPromise())
        ctx.read()
    }

    private fun handleInitJs5RemoteConnection(ctx: ChannelHandlerContext, msg: ServiceRequest.InitJs5RemoteConnection) {
        val encoder = ctx.pipeline().get(ProtocolEncoder::class.java)
        encoder.protocol = js5RemoteDownstream

        if (msg.build != Revision.MAJOR) {
            ctx.write(Js5Response.ClientOutOfDate).addListener(ChannelFutureListener.CLOSE)
            return
        }
        ctx.pipeline().addLast(
            XorDecoder(),
            Js5RequestDecoder(),
            Js5GroupResponseEncoder,
            js5HandlerProvider.get()
        )
        /* js5 connection no longer uses standard protocol codec */
        ctx.pipeline().remove(ProtocolDecoder::class.java)
        ctx.write(Js5Response.Ok).addListener { future ->
            if (future.isSuccess) {
                ctx.pipeline().remove(encoder)
                ctx.pipeline().remove(this)
            }
        }
    }

    private fun handleGameLogin(ctx: ChannelHandlerContext, msg: ServiceRequest.GameLogin) = with(msg) {
        val encoder = ctx.pipeline().get(ProtocolEncoder::class.java)
        encoder.protocol = loginDownstream

        if (buildMajor != Revision.MAJOR || buildMinor != Revision.MINOR) {
            ctx.write(LoginResponse.ClientOutOfDate).addListener(ChannelFutureListener.CLOSE)
            return
        } else if (encrypted.seed != serverKey) {
            ctx.write(LoginResponse.BadSessionId).addListener(ChannelFutureListener.CLOSE)
            return
        } else if (machineInfo.version != Revision.MACHINE_INFO_HEADER) {
            ctx.write(LoginResponse.ClientProtocolOutOfDate).addListener(ChannelFutureListener.CLOSE)
            return
        }
        scope.launch {
            // TODO: account dispatcher to hold all this boilerplate
            val request = PlayerDataRequest(
                username = username,
                plaintTextPass = encrypted.password,
                loginXtea = encrypted.xtea.toIntArray()
            )
            when (val deserialize = playerCodec.deserialize(request)) {
                PlayerDataResponse.InvalidCredentials -> {
                    // TODO: invalid credentials response
                    ctx.write(LoginResponse.ClientProtocolOutOfDate)
                        .addListener(ChannelFutureListener.CLOSE)
                }
                is PlayerDataResponse.Exception -> {
                    logger.error(deserialize.t) { "Exception thrown when deserializing player: $username" }
                }
                is PlayerDataResponse.Success -> {
                    val player = deserialize.player
                    /* instantly save new players */
                    if (deserialize is PlayerDataResponse.Success.NewPlayer) {
                        launch { playerCodec.serialize(player) }
                    }
                    val decodeCipher = IsaacRandom(encrypted.xtea.toIntArray())
                    val encodeCipher = IsaacRandom(encrypted.xtea.toIntArray().map { it + 50 }.toIntArray())
                    val accountHash = Hashing.sha256().hashString(username.lowercase(Locale.US), StandardCharsets.UTF_8)
                    // TODO: use load-request response to create player in game-thread dispatcher.
                    val playerIndex = players.nextAvailableIndex()
                    if (playerIndex == null) {
                        // TODO: world full response
                        ctx.write(LoginResponse.ClientProtocolOutOfDate).addListener(ChannelFutureListener.CLOSE)
                        return@launch
                    }
                    players[playerIndex] = player
                    player.index = playerIndex
                    val deviceLinkIdentifier = when (encrypted.authType) {
                        // TODO: use player_id or account_id as identifier
                        LoginPacketRequest.AuthType.TwoFactorInputTrustDevice -> 69
                        else -> null
                    }
                    val response = LoginResponse.ConnectOk(
                        deviceLinkIdentifier = deviceLinkIdentifier,
                        playerModLevel = 2,
                        playerMember = true,
                        playerMod = true,
                        playerIndex = player.index,
                        accountHash = accountHash.asLong(),
                        cipher = encodeCipher
                    )
                    ctx.writeAndFlush(response).addListener { future ->
                        if (!future.isSuccess) return@addListener
                        val decoder = ctx.pipeline().get(ProtocolDecoder::class.java)
                        when (platform) {
                            Platform.Desktop -> {
                                encoder.protocol = gamePackets.desktopDownstream.getOrCreateProtocol()
                                decoder.protocol = gamePackets.desktopUpstream.getOrCreateProtocol()
                            }
                        }
                        val client = Client(player, ctx.channel())
                        ctx.channel().setClientAttr(client)
                        encoder.cipher = encodeCipher
                        decoder.cipher = decodeCipher
                        clients += client
                        events += ClientSession.Connect(client)
                    }
                }
            }
        }
        return@with
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            ctx.close()
        }
    }
}
