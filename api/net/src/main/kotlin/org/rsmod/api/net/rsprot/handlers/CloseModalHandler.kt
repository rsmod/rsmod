package org.rsmod.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.misc.user.CloseModal
import org.rsmod.game.entity.Player

class CloseModalHandler : MessageHandler<CloseModal> {
    override fun handle(player: Player, message: CloseModal) {
        player.requestModalClose = true
    }
}
