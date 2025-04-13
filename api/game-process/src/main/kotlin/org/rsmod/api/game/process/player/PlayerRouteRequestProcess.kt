package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.rsmod.api.player.forceDisconnect
import org.rsmod.api.utils.logging.GameExceptionHandler
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList

public class PlayerRouteRequestProcess
@Inject
constructor(
    private val players: PlayerList,
    private val movement: PlayerMovementProcessor,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        players.process()
    }

    @Suppress("DeferredResultUnused")
    private fun PlayerList.process() = runBlocking {
        supervisorScope {
            for (player in this@process) {
                val request = player.routeRequest
                if (request != null) {
                    player.routeRequest = null
                    async { player.tryOrDisconnect { movement.consumeRequest(this, request) } }
                }
            }
        }
    }

    private inline fun Player.tryOrDisconnect(block: Player.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            forceDisconnect()
            exceptionHandler.handle(e) { "Error processing route request cycle for player: $this." }
        } catch (e: NotImplementedError) {
            forceDisconnect()
            exceptionHandler.handle(e) { "Error processing route request cycle for player: $this." }
        }
}
