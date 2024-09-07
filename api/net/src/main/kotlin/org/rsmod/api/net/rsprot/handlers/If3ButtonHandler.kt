package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import net.rsprot.protocol.game.incoming.buttons.If3Button
import org.rsmod.game.entity.Player

class If3ButtonHandler : MessageHandler<If3Button> {
    private val logger = InlineLogger()

    override fun handle(player: Player, message: If3Button) {
        logger.debug { "If3Button: $message" }
    }
}
