package gg.rsmod.plugins.protocol.codec.login

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

private val logger = InlineLogger()

class LoginHandler @Inject constructor(
    private val dispatcher: LoginDispatcher
) : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is LoginRequest) {
            logger.error { "Invalid message type (message=$msg)" }
            return
        }
        dispatcher.add(msg)
    }
}
