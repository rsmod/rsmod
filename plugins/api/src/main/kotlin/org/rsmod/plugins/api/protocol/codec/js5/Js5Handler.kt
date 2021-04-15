package org.rsmod.plugins.api.protocol.codec.js5

import com.github.michaelbull.logging.InlineLogger
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.rsmod.plugins.api.protocol.codec.ResponseType
import org.rsmod.plugins.api.protocol.codec.exceptionCaught
import javax.inject.Inject

private val logger = InlineLogger()

class Js5Handler @Inject constructor(
    private val dispatcher: Js5Dispatcher
) : ChannelInboundHandlerAdapter() {

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        logger.trace { "Channel registered (channel=${ctx.channel()})" }
        super.handlerAdded(ctx)
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext) {
        logger.trace { "Channel unregistered (channel=${ctx.channel()})" }
        super.channelUnregistered(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        when (msg) {
            is Js5Request -> {
                logger.trace { "Add JS5 request (request=$msg, channel=${ctx.channel()})" }
                dispatcher.add(ctx.channel(), msg)
            }
            is ResponseType -> ctx.channel().writeAndFlush(msg)
            else -> {
                logger.error { "Invalid message type (message=$msg)" }
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.exceptionCaught(cause)
    }
}
