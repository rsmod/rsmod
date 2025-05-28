package org.rsmod.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.messaging.MessagePublic
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.player.PublicMessage

class MessagePublicHandler : MessageHandler<MessagePublic> {
    override fun handle(player: Player, message: MessagePublic) {
        val publicMessage =
            PublicMessage(
                text = message.message,
                colour = message.colour,
                effect = message.effect,
                clanType = if (message.clanType == -1) null else message.clanType,
                modIcon = player.modLevel.clientCode,
                autoTyper = false,
                pattern = message.pattern?.asByteArray(),
            )
        player.publicMessage = publicMessage
    }
}
