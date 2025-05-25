package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.game.process.npc.hunt.NpcHuntProcessor
import org.rsmod.api.game.process.npc.mode.NpcModeProcessor
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.repo.NpcRevealProcessor
import org.rsmod.api.utils.logging.GameExceptionHandler
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList

public class NpcMainProcess
@Inject
constructor(
    private val npcList: NpcList,
    private val registry: NpcRegistry,
    private val reveal: NpcRevealProcessor,
    private val hunt: NpcHuntProcessor,
    private val regen: NpcRegenProcessor,
    private val aiTimers: AiTimerProcessor,
    private val timers: NpcTimerProcessor,
    private val aiQueues: AiQueueProcessor,
    private val queues: NpcQueueProcessor,
    private val modes: NpcModeProcessor,
    private val interactions: NpcInteractionProcessor,
    private val mapClock: MapClock,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        for (npc in npcList) {
            npc.processedMapClock = mapClock.cycle
            npc.previousCoords = npc.coords
            npc.tryOrDespawn {
                resumePausedProcess()
                revealProcess()
                processHunt()
                processRegen()
                processAiTimer()
                processAiQueues()
                processQueues()
                processTimers()
                processModes()
                processInteractions()
            }
        }
    }

    private fun Npc.resumePausedProcess() {
        if (canProcess) {
            advanceActiveCoroutine()
        }
    }

    private fun Npc.revealProcess() {
        reveal.process(this)
    }

    private fun Npc.processHunt() {
        if (canProcess) {
            hunt.process(this)
        }
    }

    private fun Npc.processRegen() {
        if (canProcess) {
            regen.process(this)
        }
    }

    private fun Npc.processAiTimer() {
        if (canProcess) {
            aiTimers.process(this)
        }
    }

    private fun Npc.processAiQueues() {
        if (canProcess) {
            aiQueues.process(this)
        }
    }

    private fun Npc.processQueues() {
        if (canProcess) {
            queues.process(this)
        }
    }

    private fun Npc.processTimers() {
        if (canProcess) {
            timers.process(this)
        }
    }

    private fun Npc.processModes() {
        if (canProcess) {
            modes.process(this)
        }
    }

    private fun Npc.processInteractions() {
        if (canProcess) {
            interactions.process(this)
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
