package org.rsmod.plugins.api.protocol.codec.login

import com.github.michaelbull.logging.InlineLogger
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.rsmod.plugins.api.protocol.codec.ResponseType
import org.rsmod.plugins.api.protocol.codec.account.AccountDispatcher
import org.rsmod.plugins.api.protocol.codec.exceptionCaught

private val logger = InlineLogger()

class LoginHandler(
    private val dispatcher: AccountDispatcher
) : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        when (msg) {
            is LoginRequest -> dispatcher.register(msg)
            is ResponseType -> ctx.channel().writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE)
            else -> logger.error { "Invalid message type (message=$msg)" }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.exceptionCaught(cause)
    }
}
