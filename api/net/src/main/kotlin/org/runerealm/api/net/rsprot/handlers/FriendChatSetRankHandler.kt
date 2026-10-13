package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.friendchat.FriendChatSetRank
import org.runerealm.api.player.events.social.FriendChatSetRank as SetRankEvent
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player

class FriendChatSetRankHandler @Inject constructor(private val eventBus: EventBus) :
    MessageHandler<FriendChatSetRank> {
    override fun handle(player: Player, message: FriendChatSetRank) {
        eventBus.publish(SetRankEvent(player, message.name, message.rank))
    }
}
