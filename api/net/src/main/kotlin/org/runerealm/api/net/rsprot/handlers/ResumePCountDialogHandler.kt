package org.runerealm.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.resumed.ResumePCountDialog
import org.runerealm.api.player.input.ResumePCountDialogInput
import org.runerealm.game.entity.Player

class ResumePCountDialogHandler : MessageHandler<ResumePCountDialog> {
    override fun handle(player: Player, message: ResumePCountDialog) {
        val input = ResumePCountDialogInput(message.count)
        player.resumeActiveCoroutine(input)
    }
}
