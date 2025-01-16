package org.rsmod.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.resumed.ResumePStringDialog
import org.rsmod.api.player.input.ResumePStringDialogInput
import org.rsmod.game.entity.Player

class ResumePStringDialogHandler : MessageHandler<ResumePStringDialog> {
    override fun handle(player: Player, message: ResumePStringDialog) {
        val input = ResumePStringDialogInput(message.string)
        player.resumeActiveCoroutine(input)
    }
}
