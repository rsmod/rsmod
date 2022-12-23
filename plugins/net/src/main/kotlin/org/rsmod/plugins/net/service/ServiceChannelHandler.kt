package org.rsmod.plugins.net.service

import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateEvent
import org.openrs2.crypto.secureRandom
import org.rsmod.plugins.net.js5.Js5ChannelHandler
import org.rsmod.plugins.net.js5.downstream.Js5GroupResponseEncoder
import org.rsmod.plugins.net.js5.downstream.Js5RemoteDownstream
import org.rsmod.plugins.net.js5.downstream.Js5Response
import org.rsmod.plugins.net.js5.downstream.XorDecoder
import org.rsmod.plugins.net.js5.upstream.Js5RequestDecoder
import org.rsmod.plugins.net.service.downstream.ServiceResponse
import org.rsmod.protocol.Protocol
import org.rsmod.protocol.ProtocolDecoder
import org.rsmod.protocol.ProtocolEncoder
import javax.inject.Inject
import javax.inject.Provider

class ServiceChannelHandler @Inject constructor(
    @Js5RemoteDownstream private val js5RemoteDownstream: Protocol,
    private val js5HandlerProvider: Provider<Js5ChannelHandler>
) : SimpleChannelInboundHandler<ServiceRequest>(ServiceRequest::class.java) {

    private var serverKey = 0L

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.read()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ServiceRequest) {
        when (msg) {
            ServiceRequest.InitGameConnection -> handleInitGameConnection(ctx)
            is ServiceRequest.InitJs5RemoteConnection -> handleInitJs5RemoteConnection(ctx, msg)
            is ServiceRequest.LogIn -> handleLoginConnection(ctx, msg)
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

        // TODO: configurable server js5 build
        if (msg.build != 209) {
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

    private fun handleLoginConnection(ctx: ChannelHandlerContext, msg: ServiceRequest.LogIn) = with(msg) {
        if (buildMajor != 209 || buildMinor != 1) {
            // TODO: configurable build versions
            ctx.write(ServiceResponse.ClientOutOfDate).addListener(ChannelFutureListener.CLOSE)
            return
        } else if (encrypted.seed != serverKey) {
            ctx.write(ServiceResponse.BadSessionId).addListener(ChannelFutureListener.CLOSE)
            return
        } else if (machineInfo.version != 9) {
            // TODO: configurable machine info version
            ctx.write(ServiceResponse.ClientProtocolOutOfDate).addListener(ChannelFutureListener.CLOSE)
            return
        }
        // TODO: dispatch profile load request
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
