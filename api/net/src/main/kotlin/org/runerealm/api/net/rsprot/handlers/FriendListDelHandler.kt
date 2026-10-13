package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.social.FriendListDel
import org.runerealm.api.player.events.social.SocialEvents
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player

class FriendListDelHandler @Inject constructor(private val eventBus: EventBus) :
    MessageHandler<FriendListDel> {
    override fun handle(player: Player, message: FriendListDel) {
        eventBus.publish(SocialEvents.FriendDel(player, message.name))
    }
}
