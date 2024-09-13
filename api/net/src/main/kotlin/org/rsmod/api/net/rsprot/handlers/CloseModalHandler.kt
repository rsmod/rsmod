package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.misc.user.CloseModal
import org.rsmod.api.player.ui.ifCloseModals
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player

class CloseModalHandler @Inject constructor(private val eventBus: EventBus) :
    MessageHandler<CloseModal> {
    override fun handle(player: Player, message: CloseModal) {
        player.ifCloseModals(eventBus)
    }
}
