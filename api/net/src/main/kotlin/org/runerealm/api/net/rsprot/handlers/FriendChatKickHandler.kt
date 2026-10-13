package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.friendchat.FriendChatKick
import org.runerealm.api.player.events.social.FriendChatKick as KickEvent
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player

class FriendChatKickHandler @Inject constructor(private val eventBus: EventBus) :
    MessageHandler<FriendChatKick> {
    override fun handle(player: Player, message: FriendChatKick) {
        eventBus.publish(KickEvent(player, message.name))
    }
}
