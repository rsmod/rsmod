package org.rsmod.api.game.process.player

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.api.player.clearInteractionRoute
import org.rsmod.api.player.forceDisconnect
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.interact.Interaction

public class PlayerMainProcessor
@Inject
constructor(
    private val players: PlayerList,
    private val timers: PlayerTimerProcessor,
    private val interact: PlayerInteractionProcessor,
    private val movement: PlayerMovementProcessor,
    private val facing: PlayerFaceSquareProcessor,
    private val buildAreas: PlayerBuildAreaProcessor,
    private val mapSquares: PlayerMapSquareProcessor,
    private val eventBus: EventBus,
    private val mapClock: MapClock,
) {
    private val logger = InlineLogger()

    public fun process() {
        players.process()
        players.forEach { it.tryOrDisconnect { clientProcess() } }
        eventBus.publish(GameLifecycle.PlayersProcessed)
        players.forEach { it.tryOrDisconnect { clientPostProcess() } }
    }

    @Suppress("DeferredResultUnused")
    private fun PlayerList.process() = runBlocking {
        for (player in this@process) {
            player.currentMapClock = mapClock.cycle
            player.tryOrDisconnect {
                resumePausedProcess()
                refreshFaceEntity()
                processTimers()
                processMovementSequence()
            }
        }
    }

    private fun Player.resumePausedProcess() {
        if (isNotDelayed) {
            advanceActiveCoroutine()
        }
    }

    // TODO: Why is this needed?
    private fun Player.refreshFaceEntity() {
        val interaction = interaction
        if (interaction == null) {
            resetFaceEntity()
        }
    }

    private fun Player.processTimers() {
        timers.process(this)
    }

    private fun Player.processMovementSequence() {
        preMovementInteraction(interaction)
        processInteractionMovementInterference(interaction)
        movementProcess()
        postMovementInteraction(interaction)
    }

    private fun Player.preMovementInteraction(interaction: Interaction?) {
        val interaction = interaction ?: return
        interact.processPreMovement(this, interaction)
    }

    private fun Player.processInteractionMovementInterference(interaction: Interaction?) {
        val interaction = interaction ?: return
        if (interact.isMovementCancellationRequired(interaction)) {
            clearInteractionRoute()
        }
    }

    private fun Player.movementProcess() {
        movement.process(this)
    }

    private fun Player.postMovementInteraction(interaction: Interaction?) {
        val interaction = interaction ?: return
        interact.processPostMovement(this, interaction)
    }

    private fun Player.clientProcess() {
        faceSquareProcess()
        mapListenerProcess()
        client.preparePlayerCycle(this)
        client.playerCycle(this)
    }

    private fun Player.faceSquareProcess() {
        facing.process(this)
    }

    private fun Player.mapListenerProcess() {
        buildAreas.process(this)
        mapSquares.process(this)
    }

    private fun Player.clientPostProcess() {
        client.completePlayerCycle(this)
    }

    private fun Player.tryOrDisconnect(block: Player.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            forceDisconnect()
            logger.error(e) { "Error processing main cycle for player: $this." }
        }
}
