package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.api.player.forceDisconnect
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.ui.closeSubs
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.utils.logging.GameExceptionHandler
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.interact.Interaction
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.ui.Component

public class PlayerMainProcess
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val players: PlayerList,
    private val queues: PlayerQueueProcessor,
    private val timers: PlayerTimerProcessor,
    private val interact: PlayerInteractionProcessor,
    private val movement: PlayerMovementProcessor,
    private val facing: PlayerFaceSquareProcessor,
    private val buildAreas: PlayerBuildAreaProcessor,
    private val mapSquares: PlayerMapSquareProcessor,
    private val regions: PlayerRegionProcessor,
    private val eventBus: EventBus,
    private val mapClock: MapClock,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        players.process()
        players.forEach { it.tryOrDisconnect { clientProcess() } }
        eventBus.publish(GameLifecycle.PlayersProcessed)
        players.forEach { it.tryOrDisconnect { clientPostProcess() } }
    }

    private fun PlayerList.process() = runBlocking {
        for (player in this@process) {
            player.processedMapClock = mapClock.cycle
            player.tryOrDisconnect {
                resumePausedProcess()
                refreshFaceEntity()
                processIfCloseQueue()
                processIfCloseModal()
                processQueues()
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

    private fun Player.refreshFaceEntity() {
        val interaction = interaction
        if (interaction == null) {
            resetFaceEntity()
        }
    }

    private fun Player.processIfCloseQueue() {
        for (target in ui.closeQueue.intIterator()) {
            val component = Component(target)
            closeSubs(component, eventBus)
        }
        ui.closeQueue.clear()
    }

    private fun Player.processIfCloseModal() {
        if (ui.closeModal) {
            ui.closeModal = false
            ifClose(eventBus)
        }
    }

    private fun Player.processQueues() {
        queues.process(this)
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
            clearInteraction()
            clearRouteRecalc()
            clearMapFlag()
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
        facing.process(this)
        buildAreas.process(this)
        mapSquares.process(this)
        regions.process(this)
        clientCycle.preCycle(this)
    }

    private fun Player.clientPostProcess() {
        clientCycle.postCycle(this)
    }

    private inline fun Player.tryOrDisconnect(block: Player.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            forceDisconnect()
            exceptionHandler.handle(e) { "Error processing main cycle for player: $this." }
        } catch (e: NotImplementedError) {
            forceDisconnect()
            exceptionHandler.handle(e) { "Error processing main cycle for player: $this." }
        }
}
