package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.rsmod.api.player.forceDisconnect
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
    private val mapClock: MapClock,
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val players: PlayerList,
    private val queues: PlayerQueueProcessor,
    private val timers: PlayerTimerProcessor,
    private val engineQueues: PlayerEngineQueueProcessor,
    private val interact: PlayerInteractionProcessor,
    private val movement: PlayerMovementProcessor,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        processAll()
    }

    private fun processAll() = runBlocking {
        for (player in players) {
            player.processedMapClock = mapClock.cycle
            player.tryOrDisconnect {
                resumePausedProcess()
                refreshFaceEntity()
                processIfCloseQueue()
                processIfCloseModal()
                processQueues()
                processTimers()
                processEngineQueues()
                processMovementSequence()
                processIfCloseDisconnect()
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

    private fun Player.processEngineQueues() {
        engineQueues.process(this)
    }

    private fun Player.processMovementSequence() {
        // Store the current interaction at this stage to ensure that if an interaction triggers a
        // new one (e.g., combat calling `opnpc2`), the original interaction completes before the
        // new one is processed.
        val interaction = interaction
        preMovementInteraction(interaction)
        movementProcess()
        postMovementInteraction(interaction)
    }

    private fun Player.preMovementInteraction(interaction: Interaction?) {
        val interaction = interaction ?: return
        interact.processPreMovement(this, interaction)
    }

    private fun Player.movementProcess() {
        movement.process(this)
    }

    private fun Player.postMovementInteraction(interaction: Interaction?) {
        val interaction = interaction ?: return
        interact.processPostMovement(this, interaction)
    }

    private fun Player.processIfCloseDisconnect() {
        if (isPendingDisconnect()) {
            ifClose(eventBus)
        }
    }

    private fun Player.isPendingDisconnect(): Boolean = clientDisconnected.get() || forceDisconnect

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
