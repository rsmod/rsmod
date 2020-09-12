package gg.rsmod.plugins.protocol.codec.js5

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import gg.rsmod.plugins.protocol.codec.exceptionCaught
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

private val logger = InlineLogger()

class Js5Handler @Inject constructor(
    private val dispatcher: Js5Dispatcher
) : ChannelInboundHandlerAdapter() {

    override fun channelRegistered(ctx: ChannelHandlerContext) {
        logger.debug { "Channel registered (channel=${ctx.channel()})" }
        super.channelRegistered(ctx)
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext) {
        logger.debug { "Channel unregistered (channel=${ctx.channel()})" }
        super.channelUnregistered(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is Js5Request) {
            logger.error { "Invalid message type (message=$msg)" }
            return
        }
        logger.trace { "Add JS5 request (request=$msg, channel=${ctx.channel()})" }
        dispatcher.add(ctx.channel(), msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.exceptionCaught(cause)
    }
}
