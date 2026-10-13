package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.friendchat.FriendChatJoinLeave
import org.runerealm.api.player.events.social.FriendChatJoinLeave as JoinLeaveEvent
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player

class FriendChatJoinLeaveHandler @Inject constructor(private val eventBus: EventBus) :
    MessageHandler<FriendChatJoinLeave> {
    override fun handle(player: Player, message: FriendChatJoinLeave) {
        eventBus.publish(JoinLeaveEvent(player, message.name))
    }
}
