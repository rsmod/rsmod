package org.runerealm.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.misc.user.CloseModal
import org.runerealm.annotations.InternalApi
import org.runerealm.game.entity.Player

class CloseModalHandler : MessageHandler<CloseModal> {
    @OptIn(InternalApi::class)
    override fun handle(player: Player, message: CloseModal) {
        player.ui.closeModal = true
    }
}
