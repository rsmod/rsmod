package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.social.FriendListAdd
import org.runerealm.api.player.events.social.SocialEvents
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player

class FriendListAddHandler @Inject constructor(private val eventBus: EventBus) :
    MessageHandler<FriendListAdd> {
    override fun handle(player: Player, message: FriendListAdd) {
        eventBus.publish(SocialEvents.FriendAdd(player, message.name))
    }
}
