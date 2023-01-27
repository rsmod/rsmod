package org.rsmod.plugins.net.service

import com.google.common.hash.Hashing
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import org.openrs2.crypto.secureRandom
import org.rsmod.plugins.net.game.client.Platform
import org.rsmod.plugins.net.js5.Js5ChannelHandler
import org.rsmod.plugins.net.js5.downstream.Js5GroupResponseEncoder
import org.rsmod.plugins.net.js5.downstream.Js5RemoteDownstream
import org.rsmod.plugins.net.js5.downstream.Js5Response
import org.rsmod.plugins.net.js5.downstream.XorDecoder
import org.rsmod.plugins.net.js5.upstream.Js5RequestDecoder
import org.rsmod.plugins.net.login.downstream.LoginDownstream
import org.rsmod.plugins.net.login.downstream.LoginResponse
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

class ServiceChannelHandler @Inject constructor(
    private val js5HandlerProvider: Provider<Js5ChannelHandler>,
    @Js5RemoteDownstream private val js5RemoteDownstream: Protocol,
    @LoginDownstream private val loginDownstream: Protocol,
    private val gamePackets: GamePlatformPacketMaps
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
        // TODO: dispatch profile load request
        val accountHash = Hashing.sha256().hashString(username.lowercase(Locale.US), StandardCharsets.UTF_8)
        val response = LoginResponse.ConnectOk(
            rememberDevice = true,
            playerModLevel = 2,
            playerMember = true,
            playerMod = true,
            playerIndex = 1,
            accountHash = accountHash.asLong()
        )
        ctx.write(response).addListener { future ->
            if (!future.isSuccess) return@addListener
            val decoder = ctx.pipeline().get(ProtocolDecoder::class.java)
            when (platform) {
                Platform.Desktop -> {
                    encoder.protocol = gamePackets.desktopDownstream.getOrCreateProtocol()
                    decoder.protocol = gamePackets.desktopUpstream.getOrCreateProtocol()
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
