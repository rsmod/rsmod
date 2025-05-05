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
    private val areas: PlayerAreaProcessor,
    private val engineQueues: PlayerEngineQueueProcessor,
    private val interact: PlayerInteractionProcessor,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        processAll()
    }

    private fun processAll() = runBlocking {
        for (player in players) {
            // Skip players who are no longer considered online. They remain in the player list
            // temporarily until their account data has been saved.
            if (!player.canProcess) {
                continue
            }
            player.processedMapClock = mapClock.cycle
            player.tryOrDisconnect {
                resumePausedProcess()
                refreshFaceEntity()
                processIfCloseQueue()
                processIfCloseModal()
                processQueues()
                processTimers()
                processAreas()
                processEngineQueues()
                processInteractions()
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

    private fun Player.processAreas() {
        areas.process(this)
    }

    private fun Player.processEngineQueues() {
        engineQueues.process(this)
    }

    // Interactions implicitly handle movement processing as well.
    private fun Player.processInteractions() {
        // Do not process interactions while "fake-logged."
        if (pendingLogout) {
            clearInteraction()
            return
        }
        interact.process(this)
    }

    private fun Player.processIfCloseDisconnect() {
        if (isPendingDisconnect()) {
            ifClose(eventBus)
        }
    }

    private fun Player.isPendingDisconnect(): Boolean {
        return clientDisconnected.get() || forceDisconnect || pendingShutdown
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
