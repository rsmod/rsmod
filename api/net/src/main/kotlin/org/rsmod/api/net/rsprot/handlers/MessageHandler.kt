package org.rsmod.api.net.rsprot.handlers

import net.rsprot.protocol.message.IncomingGameMessage
import net.rsprot.protocol.message.codec.incoming.MessageConsumer
import org.rsmod.game.entity.Player

fun interface MessageHandler<in T : IncomingGameMessage> : MessageConsumer<Player, T> {
    fun handle(player: Player, message: T)

    override fun consume(t: Player, u: T) {
        handle(t, u)
    }
}
