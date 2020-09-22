package gg.rsmod.plugins.protocol.codec.login

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.plugins.protocol.codec.account.AccountDispatcher
import gg.rsmod.plugins.protocol.codec.exceptionCaught
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

private val logger = InlineLogger()

class LoginHandler(
    private val dispatcher: AccountDispatcher
) : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is LoginRequest) {
            logger.error { "Invalid message type (message=$msg)" }
            return
        }
        dispatcher.register(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.exceptionCaught(cause)
    }
}
