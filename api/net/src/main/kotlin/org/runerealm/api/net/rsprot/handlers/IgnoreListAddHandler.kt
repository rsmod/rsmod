package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.social.IgnoreListAdd
import org.runerealm.api.player.events.social.SocialEvents
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player

class IgnoreListAddHandler @Inject constructor(private val eventBus: EventBus) :
    MessageHandler<IgnoreListAdd> {
    override fun handle(player: Player, message: IgnoreListAdd) {
        eventBus.publish(SocialEvents.IgnoreAdd(player, message.name))
    }
}
