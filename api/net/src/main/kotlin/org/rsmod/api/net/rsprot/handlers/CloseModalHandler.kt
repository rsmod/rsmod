package org.rsmod.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.misc.user.CloseModal
import org.rsmod.annotations.InternalApi
import org.rsmod.game.entity.Player

class CloseModalHandler : MessageHandler<CloseModal> {
    @OptIn(InternalApi::class)
    override fun handle(player: Player, message: CloseModal) {
        player.ui.closeModal = true
    }
}
