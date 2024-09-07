package org.rsmod.api.game.process.player

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.rsmod.api.player.forceDisconnect
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList

public class PlayerRouteRequestProcess
@Inject
constructor(private val players: PlayerList, private val movement: PlayerMovementProcessor) {
    private val logger = InlineLogger()

    public fun process() {
        players.process()
    }

    private fun PlayerList.process() = runBlocking {
        supervisorScope {
            for (player in this@process) {
                player.previousCoords = player.coords
                val request = player.routeRequest
                if (request != null) {
                    player.routeRequest = null
                    async { player.tryOrDisconnect { movement.consumeRequest(this, request) } }
                }
            }
        }
    }

    private fun Player.tryOrDisconnect(block: Player.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            forceDisconnect()
            logger.error(e) { "Error processing route request cycle for player: $this." }
        }
}
