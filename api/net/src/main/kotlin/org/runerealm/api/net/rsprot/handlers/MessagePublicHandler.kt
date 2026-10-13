package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.messaging.MessagePublic
import org.runerealm.api.player.events.social.ClanChatMessage
import org.runerealm.api.player.events.social.FriendChatMessage
import org.runerealm.api.player.ironman.chatIcon
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.player.PublicMessage

class MessagePublicHandler @Inject constructor(private val eventBus: EventBus) :
    MessageHandler<MessagePublic> {
    override fun handle(player: Player, message: MessagePublic) {
        when (message.type) {
            FRIEND_CHAT_TYPE -> {
                eventBus.publish(FriendChatMessage(player, message.message))
                return
            }
            CLAN_CHAT_TYPE -> {
                eventBus.publish(ClanChatMessage(player, message.message, guest = false))
                return
            }
            GUEST_CLAN_CHAT_TYPE -> {
                eventBus.publish(ClanChatMessage(player, message.message, guest = true))
                return
            }
        }
        val publicMessage =
            PublicMessage(
                text = message.message,
                colour = message.colour,
                effect = message.effect,
                clanType = if (message.clanType == -1) null else message.clanType,
                modIcon = player.chatIcon,
                autoTyper = false,
                pattern = message.pattern?.asByteArray(),
            )
        player.publicMessage = publicMessage
    }

    private companion object {
        /** Public chat typed with a leading "/" is sent to the player's friends chat channel. */
        const val FRIEND_CHAT_TYPE = 2

        /** Public chat typed with a leading "//" is sent to the player's clan channel. */
        const val CLAN_CHAT_TYPE = 3

        /** Public chat typed with a leading "///" is sent to the player's guest clan channel. */
        const val GUEST_CLAN_CHAT_TYPE = 4
    }
}
