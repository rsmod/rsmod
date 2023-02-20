package org.rsmod.plugins.net.service

import com.github.michaelbull.logging.InlineLogger
import com.github.michaelbull.retry.RetryFailure
import com.github.michaelbull.retry.RetryInstruction
import com.github.michaelbull.retry.policy.binaryExponentialBackoff
import com.github.michaelbull.retry.policy.limitAttempts
import com.github.michaelbull.retry.policy.plus
import com.github.michaelbull.retry.retry
import com.google.common.hash.Hashing
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.openrs2.cache.Js5MasterIndex
import org.openrs2.crypto.IsaacRandom
import org.openrs2.crypto.secureRandom
import org.rsmod.game.client.Client
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.net.client.Platform
import org.rsmod.plugins.api.net.login.LoginPacketRequest
import org.rsmod.plugins.api.net.platform.GamePlatformPacketMaps
import org.rsmod.plugins.api.prot.Revision
import org.rsmod.plugins.net.clientAttr
import org.rsmod.plugins.net.js5.Js5ChannelHandler
import org.rsmod.plugins.net.js5.downstream.Js5GroupResponseEncoder
import org.rsmod.plugins.net.js5.downstream.Js5RemoteDownstream
import org.rsmod.plugins.net.js5.downstream.Js5Response
import org.rsmod.plugins.net.js5.downstream.XorDecoder
import org.rsmod.plugins.net.js5.upstream.Js5RequestDecoder
import org.rsmod.plugins.net.login.downstream.LoginDownstream
import org.rsmod.plugins.net.login.downstream.LoginResponse
import org.rsmod.plugins.net.service.downstream.ServiceResponse
import org.rsmod.plugins.net.service.upstream.ServiceRequest
import org.rsmod.plugins.net.setClientAttr
import org.rsmod.plugins.profile.dispatch.client.ClientDeregisterDispatch
import org.rsmod.plugins.profile.dispatch.client.ClientDispatchRequest
import org.rsmod.plugins.profile.dispatch.client.ClientRegisterDispatch
import org.rsmod.plugins.profile.dispatch.player.PlayerDeregisterDispatch
import org.rsmod.plugins.profile.dispatch.player.PlayerDispatchRequest
import org.rsmod.plugins.profile.dispatch.player.PlayerRegisterDispatch
import org.rsmod.plugins.profile.dispatch.player.PlayerRegisterResponse
import org.rsmod.plugins.profile.dispatch.transaction.await
import org.rsmod.plugins.store.player.PlayerCodec
import org.rsmod.plugins.store.player.PlayerDataRequest
import org.rsmod.plugins.store.player.PlayerDataResponse
import org.rsmod.protocol.game.Protocol
import org.rsmod.protocol.game.ProtocolDecoder
import org.rsmod.protocol.game.ProtocolEncoder
import org.rsmod.protocol.game.packet.UpstreamPacket
import java.nio.charset.StandardCharsets
import java.util.Locale
import javax.inject.Inject
import javax.inject.Provider

private val logger = InlineLogger()

public class ServiceChannelHandler @Inject constructor(
    @Js5RemoteDownstream private val js5RemoteDownstream: Protocol,
    @LoginDownstream private val loginDownstream: Protocol,
    private val playerCodec: PlayerCodec,
    private val playerRegister: PlayerRegisterDispatch,
    private val playerDeregister: PlayerDeregisterDispatch,
    private val clientRegister: ClientRegisterDispatch,
    private val clientDeregister: ClientDeregisterDispatch,
    private val js5HandlerProvider: Provider<Js5ChannelHandler>,
    private val gamePackets: GamePlatformPacketMaps,
    private val js5MasterIndex: Js5MasterIndex
) : SimpleChannelInboundHandler<UpstreamPacket>(UpstreamPacket::class.java) {

    private lateinit var scope: CoroutineScope
    private var serverKey = 0L

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        val exceptionHandler = CoroutineExceptionHandler { _, ex -> ctx.fireExceptionCaught(ex) }
        scope = CoroutineScope(ctx.executor().asCoroutineDispatcher() + exceptionHandler)
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        val client = ctx.channel().clientAttr() ?: return
        clientDeregister.query(ClientDispatchRequest(client))
        scope.launch { awaitPlayerSave(client.player) }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.read()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: UpstreamPacket) {
        when (msg) {
            ServiceRequest.InitGameConnection -> handleInitGameConnection(ctx)
            is ServiceRequest.InitJs5RemoteConnection -> handleInitJs5RemoteConnection(ctx, msg)
            is ServiceRequest.GameLogin -> handleGameLogIn(ctx, msg)
            else -> handleUpstreamPacket(ctx, msg)
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

    private fun handleGameLogIn(ctx: ChannelHandlerContext, msg: ServiceRequest.GameLogin) = with(msg) {
        val encoder = ctx.pipeline().get(ProtocolEncoder::class.java)
        val decoder = ctx.pipeline().get(ProtocolDecoder::class.java)
        encoder.protocol = loginDownstream

        if (buildMajor != Revision.MAJOR || buildMinor != Revision.MINOR) {
            ctx.writeAndClose(LoginResponse.ClientOutOfDate)
            return
        } else if (encrypted.seed != serverKey) {
            ctx.writeAndClose(LoginResponse.BadSessionId)
            return
        } else if (machineInfo.version != Revision.LOGIN_MACHINE_INFO_HEADER) {
            ctx.writeAndClose(LoginResponse.ClientProtocolOutOfDate)
            return
        } else if (isChecksumOutdated(cacheChecksum)) {
            ctx.writeAndClose(LoginResponse.ClientOutOfDate)
            return
        }
        scope.launch {
            val request = PlayerDataRequest(username, encrypted.password, encrypted.xtea.toIntArray())
            when (val deserialize = playerCodec.deserialize(request)) {
                PlayerDataResponse.InvalidCredentials -> ctx.writeAndClose(LoginResponse.InvalidCredentials)
                is PlayerDataResponse.Exception -> {
                    ctx.writeAndClose(LoginResponse.CouldNotComplete)
                    logger.error(deserialize.t) { "Exception thrown when deserializing player: $username" }
                }
                is PlayerDataResponse.Success -> {
                    val player = deserialize.player
                    val encodeCipher = IsaacRandom(msg.encrypted.xtea.toIntArray().map { it + 50 }.toIntArray())
                    val response = awaitPlayerResponse(player, encodeCipher, msg)
                    if (response !is LoginResponse.ConnectOk) {
                        ctx.writeAndClose(response)
                        return@launch
                    }
                    ctx.write(response, ctx.voidPromise())
                    when (msg.platform) {
                        Platform.Desktop -> {
                            encoder.protocol = gamePackets.desktopDownstream.getOrCreateProtocol()
                            decoder.protocol = gamePackets.desktopUpstream.getOrCreateProtocol()
                        }
                    }
                    val decodeCipher = IsaacRandom(msg.encrypted.xtea.toIntArray())
                    val client = Client(player, ctx.channel())
                    ctx.channel().setClientAttr(client)
                    encoder.cipher = encodeCipher
                    decoder.cipher = decodeCipher
                    clientRegister.query(ClientDispatchRequest(client))
                    ctx.read()
                }
            }
        }
        return@with
    }

    private fun handleUpstreamPacket(ctx: ChannelHandlerContext, msg: UpstreamPacket) {
        val client = ctx.channel().clientAttr() ?: return
        client.player.upstream += msg
        ctx.read()
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            ctx.close()
        }
    }

    private fun isChecksumOutdated(checksum: IntArray): Boolean {
        val js5Entries = js5MasterIndex.entries
        val mismatch = checksum.filterIndexed { index, crc -> crc != 0 && crc != js5Entries[index].checksum }
        return mismatch.isNotEmpty()
    }

    private fun ChannelHandlerContext.writeAndClose(response: LoginResponse) {
        writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    }

    private suspend fun awaitPlayerResponse(
        player: Player,
        encodeCipher: IsaacRandom,
        msg: ServiceRequest.GameLogin
    ): LoginResponse {
        val usernameHash = Hashing.sha256().hashString(player.username.lowercase(Locale.US), StandardCharsets.UTF_8)
        val registerQuery = playerRegister.query(PlayerDispatchRequest(player))
        logger.trace { "Sent query for player registration: $player." }
        val registerResponse = registerQuery.await()
        logger.debug { "Receive ${registerResponse.javaClass.simpleName} registration response for player $player." }
        if (registerResponse == PlayerRegisterResponse.NoAvailableIndex) {
            return LoginResponse.WorldIsFull
        } else if (registerResponse == PlayerRegisterResponse.AlreadyOnline) {
            return LoginResponse.AlreadyOnline
        }
        // TODO: use player_id or account_id as identifier
        val deviceLinkIdentifier = if (msg.encrypted.authType.trustDevice) 69 else null
        return LoginResponse.ConnectOk(
            deviceLinkIdentifier = deviceLinkIdentifier,
            playerModLevel = 2,
            playerMember = true,
            playerMod = true,
            playerIndex = player.index,
            accountHash = usernameHash.asLong(),
            cipher = encodeCipher
        )
    }

    private suspend fun awaitPlayerSave(player: Player) {
        var retries = 0
        retry(SERIALIZE_RETRY_POLICY) {
            logger.debug { "Serializing player $player retry attempt#${retries++}" }
            playerCodec.serialize(player)
            playerDeregister.query(PlayerDispatchRequest(player))
        }
    }

    private val LoginPacketRequest.AuthType.trustDevice: Boolean
        get() = this == LoginPacketRequest.AuthType.TwoFactorInputTrustDevice

    private companion object {

        private const val SERIALIZE_ATTEMPTS = 5
        private const val BACKOFF_BASE = 100L
        private const val BACKOFF_MAX = 10000L

        private val SERIALIZE_RETRY_POLICY: suspend RetryFailure<Throwable>.() -> RetryInstruction =
            limitAttempts(SERIALIZE_ATTEMPTS) + binaryExponentialBackoff(BACKOFF_BASE, BACKOFF_MAX)
    }
}
