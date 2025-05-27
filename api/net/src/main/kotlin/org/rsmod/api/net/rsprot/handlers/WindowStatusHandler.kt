package org.rsmod.api.net.rsprot.handlers

import net.rsprot.protocol.game.incoming.misc.client.WindowStatus
import org.rsmod.annotations.InternalApi
import org.rsmod.game.entity.Player

class WindowStatusHandler : MessageHandler<WindowStatus> {
    @OptIn(InternalApi::class)
    override fun handle(player: Player, message: WindowStatus) {
        val mode = message.windowMode
        val width = message.frameWidth
        val height = message.frameHeight
        player.ui.setWindowStatus(mode = mode, width = width, height = height)
    }
}
