package org.rsmod.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.misc.client.WindowStatus
import org.rsmod.game.entity.Player

class WindowStatusHandler : MessageHandler<WindowStatus> {
    override fun handle(player: Player, message: WindowStatus) {
        val width = message.frameWidth
        val height = message.frameHeight
        val resizable = message.windowMode == 2
        player.ui.setWindowStatus(width = width, height = height, resizable = resizable)
    }
}
