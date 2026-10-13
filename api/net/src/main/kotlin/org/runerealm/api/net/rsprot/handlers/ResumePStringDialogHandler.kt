package org.runerealm.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.resumed.ResumePStringDialog
import org.runerealm.api.player.input.ResumePStringDialogInput
import org.runerealm.game.entity.Player

class ResumePStringDialogHandler : MessageHandler<ResumePStringDialog> {
    override fun handle(player: Player, message: ResumePStringDialog) {
        val input = ResumePStringDialogInput(message.string)
        player.resumeActiveCoroutine(input)
    }
}
