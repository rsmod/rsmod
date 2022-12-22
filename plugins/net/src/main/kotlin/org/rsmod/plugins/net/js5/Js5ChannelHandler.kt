package org.rsmod.plugins.net.js5

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateEvent
import org.rsmod.plugins.net.js5.upstream.Js5Request

class Js5ChannelHandler : SimpleChannelInboundHandler<Js5Request>(Js5Request::class.java) {

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        TODO()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Js5Request) {
        TODO()
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        TODO()
    }

    override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {
        TODO()
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            ctx.close()
        }
    }
}
