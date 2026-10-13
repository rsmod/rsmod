package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import org.runerealm.api.player.forceDisconnect
import org.runerealm.api.utils.logging.GameExceptionHandler
import org.runerealm.game.MapClock
import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.util.ShuffledPlayerList

public class PlayerInputProcess
@Inject
constructor(
    private val mapClock: MapClock,
    private val players: ShuffledPlayerList,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        for (player in players) {
            player.tryOrDisconnect {
                updateMapClock()
                assignPrevCoords()
                readClientInput()
            }
        }
    }

    /**
     * Updates the player's `currentMapClock` to match the latest value from the global `mapClock`.
     *
     * This ensures that any `delay` calls set by an incoming packet (e.g., an `IfButton` click for
     * an emote using delays) use an up-to-date reference for the map clock. Without this update,
     * delays would appear to be "sped up" by one cycle because the `delay` functions rely on the
     * [PathingEntity.currentMapClock] as a baseline to calculate their delay duration.
     */
    private fun Player.updateMapClock() {
        currentMapClock = mapClock.cycle
    }

    private fun Player.assignPrevCoords() {
        previousCoords = coords
    }

    private fun Player.readClientInput() {
        client.read(this)
    }

    private inline fun Player.tryOrDisconnect(block: Player.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            forceDisconnect()
            exceptionHandler.handle(e) { "Error processing input cycle for player: $this." }
        } catch (e: NotImplementedError) {
            forceDisconnect()
            exceptionHandler.handle(e) { "Error processing input cycle for player: $this." }
        }
}
