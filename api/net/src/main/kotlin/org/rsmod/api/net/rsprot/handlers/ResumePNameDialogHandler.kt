package org.rsmod.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.resumed.ResumePNameDialog
import org.rsmod.api.player.input.ResumePNameDialogInput
import org.rsmod.game.entity.Player

class ResumePNameDialogHandler : MessageHandler<ResumePNameDialog> {
    override fun handle(player: Player, message: ResumePNameDialog) {
        val input = ResumePNameDialogInput(message.name)
        player.resumeActiveCoroutine(input)
    }
}
