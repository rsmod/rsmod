package org.rsmod.api.net.rsprot.provider

import io.netty.channel.ChannelHandlerContext
import net.rsprot.protocol.api.ChannelExceptionHandler
import net.rsprot.protocol.api.IncomingGameMessageConsumerExceptionHandler
import net.rsprot.protocol.api.Session
import net.rsprot.protocol.api.handlers.ExceptionHandlers
import net.rsprot.protocol.message.IncomingGameMessage
import org.rsmod.game.entity.Player

object ExceptionHandlersProvider {
    fun provide(): ExceptionHandlers<Player> {
        val channelHandler = ChannelExceptionHandler { _: ChannelHandlerContext, cause: Throwable ->
            throw cause
        }
        val messageHandler =
            IncomingGameMessageConsumerExceptionHandler {
                _: Session<Player>,
                _: IncomingGameMessage,
                throwable: Throwable ->
                throw throwable
            }
        return ExceptionHandlers(channelHandler, messageHandler)
    }
}
