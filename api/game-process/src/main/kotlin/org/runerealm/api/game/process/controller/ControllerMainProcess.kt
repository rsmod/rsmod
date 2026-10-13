package org.runerealm.api.game.process.controller

import jakarta.inject.Inject
import org.runerealm.api.repo.controller.ControllerRepository
import org.runerealm.api.utils.logging.GameExceptionHandler
import org.runerealm.game.MapClock
import org.runerealm.game.entity.Controller
import org.runerealm.game.entity.ControllerList

public class ControllerMainProcess
@Inject
constructor(
    private val controllerList: ControllerList,
    private val conRepo: ControllerRepository,
    private val aiTimers: AiConTimerProcessor,
    private val queues: ControllerQueueProcessor,
    private val timers: ControllerTimerProcessor,
    private val mapClock: MapClock,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        for (controller in controllerList) {
            controller.currentMapClock = mapClock.cycle
            controller.tryOrDelete {
                if (isNotDelayed) {
                    resumePausedProcess()
                    aiTimerProcess()
                    queueProcess()
                    timerProcess()
                }
            }
        }
    }

    private fun Controller.resumePausedProcess() {
        advanceActiveCoroutine()
    }

    private fun Controller.aiTimerProcess() {
        aiTimers.process(this)
    }

    private fun Controller.queueProcess() {
        queues.process(this)
    }

    private fun Controller.timerProcess() {
        timers.process(this)
    }

    private inline fun Controller.tryOrDelete(block: Controller.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            conRepo.del(this)
            exceptionHandler.handle(e) { "Error processing main cycle for controller: $this." }
        } catch (e: NotImplementedError) {
            conRepo.del(this)
            exceptionHandler.handle(e) { "Error processing main cycle for controller: $this." }
        }
}
