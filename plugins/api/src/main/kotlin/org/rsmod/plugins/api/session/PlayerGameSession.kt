package org.rsmod.plugins.api.session

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Singleton
import org.rsmod.game.model.mob.Player

private val logger = InlineLogger()

@Singleton
public class PlayerGameSession {

    public fun logIn(player: Player) {
        logger.info { "Player has logged in: $player." }
    }

    public fun logOut(player: Player) {
        logger.info { "Player has logged out: $player" }
        player.finalize()
    }
}
