package org.rsmod.plugins.net.service

import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateEvent
import org.rsmod.plugins.net.js5.Js5ChannelHandler
import org.rsmod.plugins.net.js5.downstream.Js5DownstreamResponse
import org.rsmod.plugins.net.js5.downstream.Js5GroupResponseEncoder
import org.rsmod.plugins.net.js5.downstream.Js5RemoteDownstream
import org.rsmod.plugins.net.js5.upstream.Js5RequestDecoder
import org.rsmod.protocol.Protocol
import org.rsmod.protocol.ProtocolDecoder
import org.rsmod.protocol.ProtocolEncoder
import javax.inject.Inject
import javax.inject.Provider

class ServiceChannelHandler @Inject constructor(
    @Js5RemoteDownstream private val js5RemoteDownstream: Protocol,
    private val js5HandlerProvider: Provider<Js5ChannelHandler>
) : SimpleChannelInboundHandler<ServiceRequest>(ServiceRequest::class.java) {

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.read()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ServiceRequest) {
        when (msg) {
            is ServiceRequest.InitJs5RemoteConnection -> handleMessage(ctx, msg)
            ServiceRequest.InitGameConnection -> TODO()
        }
    }

    private fun handleMessage(ctx: ChannelHandlerContext, msg: ServiceRequest.InitJs5RemoteConnection) {
        val encoder = ctx.pipeline().get(ProtocolEncoder::class.java)
        encoder.protocol = js5RemoteDownstream

        // TODO: configurable server js5 build
        if (msg.build != 209) {
            ctx.write(Js5DownstreamResponse.ClientOutOfDate).addListener(ChannelFutureListener.CLOSE)
            return
        }

        ctx.pipeline().addLast(
            Js5RequestDecoder(),
            Js5GroupResponseEncoder,
            js5HandlerProvider.get()
        )
        /* js5 connection no longer uses standard protocol codec */
        ctx.pipeline().remove(ProtocolDecoder::class.java)
        ctx.write(Js5DownstreamResponse.Ok).addListener { future ->
            if (future.isSuccess) {
                ctx.pipeline().remove(encoder)
                ctx.pipeline().remove(this)
            }
        }
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
