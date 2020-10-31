package gg.rsmod.plugins.api.protocol.codec

import com.github.michaelbull.logging.InlineLogger
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.timeout.ReadTimeoutException

private val logger = InlineLogger()

internal fun ChannelHandlerContext.exceptionCaught(cause: Throwable) {
    val timeout = cause !is ReadTimeoutException
    if (!timeout) {
        logger.error(cause) { "Channel exception caught (channel=${channel()})" }
    }
    channel().close()
}
