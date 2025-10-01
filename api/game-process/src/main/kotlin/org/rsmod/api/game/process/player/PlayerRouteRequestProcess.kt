package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.player.forceDisconnect
import org.rsmod.api.utils.logging.GameExceptionHandler
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.util.ShuffledPlayerList
import org.rsmod.map.CoordGrid

public class PlayerRouteRequestProcess
@Inject
constructor(
    private val playerList: ShuffledPlayerList,
    private val movement: PlayerMovementProcessor,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        for (player in playerList) {
            player.cacheFollowCoord()
            val request = player.routeRequest
            if (request != null && request.clientRequest) {
                player.routeRequest = null
                player.tryOrDisconnect { movement.consumeRequest(this, request) }
            }
        }
    }

    private fun Player.cacheFollowCoord() {
        val followCoord =
            if (lastProcessedCoord != CoordGrid.NULL) {
                lastProcessedCoord
            } else {
                coords.translateX(-1)
            }
        this.followCoord = followCoord
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
