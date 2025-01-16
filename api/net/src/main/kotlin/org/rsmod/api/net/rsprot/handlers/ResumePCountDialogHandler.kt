package org.rsmod.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.resumed.ResumePCountDialog
import org.rsmod.api.player.input.ResumePCountDialogInput
import org.rsmod.game.entity.Player

class ResumePCountDialogHandler : MessageHandler<ResumePCountDialog> {
    override fun handle(player: Player, message: ResumePCountDialog) {
        val input = ResumePCountDialogInput(message.count)
        player.resumeActiveCoroutine(input)
    }
}
