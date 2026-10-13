package org.runerealm.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.social.IgnoreListDel
import org.runerealm.api.player.events.social.SocialEvents
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player

class IgnoreListDelHandler @Inject constructor(private val eventBus: EventBus) :
    MessageHandler<IgnoreListDel> {
    override fun handle(player: Player, message: IgnoreListDel) {
        eventBus.publish(SocialEvents.IgnoreDel(player, message.name))
    }
}
