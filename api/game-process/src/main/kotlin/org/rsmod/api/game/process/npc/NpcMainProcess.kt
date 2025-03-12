package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.game.process.npc.mode.NpcModeProcessor
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.repo.NpcRevealProcessor
import org.rsmod.api.utils.logging.GameExceptionHandler
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.util.EntityFaceAngle

public class NpcMainProcess
@Inject
constructor(
    private val npcList: NpcList,
    private val registry: NpcRegistry,
    private val reveal: NpcRevealProcessor,
    private val aiTimers: AITimerProcessor,
    private val timers: NpcTimerProcessor,
    private val queues: NpcQueueProcessor,
    private val movement: NpcMovementProcessor,
    private val modes: NpcModeProcessor,
    private val interactions: NpcInteractionProcessor,
    private val facing: NpcFaceSquareProcessor,
    private val mapClock: MapClock,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        for (npc in npcList) {
            npc.processedMapClock = mapClock.cycle
            npc.previousCoords = npc.coords
            npc.tryOrDespawn {
                if (canProcess) {
                    resumePausedProcess()
                }
                reveal.process(this)
                if (canProcess) {
                    aiTimerProcess()
                    queueProcess()
                    timerProcess()
                    movementSequence()
                    faceSquareProcess()
                }
            }
        }
    }

    private fun Npc.resumePausedProcess() {
        advanceActiveCoroutine()
    }

    private fun Npc.aiTimerProcess() {
        aiTimers.process(this)
    }

    private fun Npc.queueProcess() {
        queues.process(this)
    }

    private fun Npc.timerProcess() {
        timers.process(this)
    }

    private fun Npc.movementSequence() {
        // Store the current interaction at this stage to ensure that if an interaction triggers a
        // new one (e.g., combat calling `opplayer2`), the original interaction completes before the
        // new one is processed.
        val interaction = interaction
        modes.process(this, interaction)
        movement.process(this)
        if (interaction != null) {
            interactions.processPostMovement(this, interaction)
        }
    }

    private fun Npc.faceSquareProcess() {
        val pending = pendingFaceSquare
        facing.process(this)
        if (pendingFaceAngle != EntityFaceAngle.NULL) {
            infoProtocol.setFaceSquare(pending.x, pending.z, instant = false)
        }
    }

    private inline fun Npc.tryOrDespawn(block: Npc.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            registry.del(this)
            exceptionHandler.handle(e) { "Error processing main cycle for npc: $this." }
        } catch (e: NotImplementedError) {
            registry.del(this)
            exceptionHandler.handle(e) { "Error processing main cycle for npc: $this." }
        }
}
