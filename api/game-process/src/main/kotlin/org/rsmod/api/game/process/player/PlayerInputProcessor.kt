package org.rsmod.api.game.process.player

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.player.forceDisconnect
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList

public class PlayerInputProcessor @Inject constructor(private val players: PlayerList) {
    private val logger = InlineLogger()

    public fun process() {
        players.forEach { player -> player.tryOrDisconnect { process() } }
    }

    private fun Player.process() {
        client.read(this)
    }

    private fun Player.tryOrDisconnect(block: Player.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            forceDisconnect()
            logger.error(e) { "Error processing input cycle for player: $this." }
        }
}
