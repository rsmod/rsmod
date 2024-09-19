package org.rsmod.api.repo.npc

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import jakarta.inject.Inject
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList

public class NpcRepository
@Inject
constructor(
    private val mapClock: MapClock,
    private val registry: NpcRegistry,
    private val npcList: NpcList,
) {
    private val addNpcs = ObjectArrayList<Npc>()
    private val delNpcs = ObjectArrayList<Npc>()

    public fun add(npc: Npc, duration: Int): Boolean {
        val added = registry.add(npc)
        if (!added) {
            return false
        }
        if (duration != Int.MAX_VALUE) {
            val deleteCycle = mapClock + duration
            npc.lifecycleDelCycle = deleteCycle
        }
        return true
    }

    public fun del(npc: Npc, duration: Int): Boolean {
        val deleted = registry.del(npc)
        if (!deleted) {
            return false
        }
        if (duration != Int.MAX_VALUE) {
            val addCycle = mapClock + duration
            npc.lifecycleAddCycle = addCycle
        }
        return true
    }

    public fun hide(npc: Npc, duration: Int) {
        registry.hide(npc)
        if (duration != Int.MAX_VALUE) {
            val revealCycle = mapClock + duration
            npc.lifecycleRevealCycle = revealCycle
        }
    }

    internal fun processReveal(npc: Npc) {
        if (npc.shouldTrigger(npc.lifecycleRevealCycle)) {
            registry.reveal(npc)
        }
    }

    internal fun processDurations() {
        computeDurations()
        processDelDurations()
        processAddDurations()
    }

    private fun computeDurations() {
        for (npc in npcList) {
            if (npc.shouldTrigger(npc.lifecycleDelCycle)) {
                delNpcs.add(npc)
            }
            if (npc.shouldTrigger(npc.lifecycleAddCycle)) {
                addNpcs.add(npc)
            }
        }
    }

    private fun processDelDurations() {
        for (npc in delNpcs) {
            registry.del(npc)
        }
        delNpcs.clear()
    }

    private fun processAddDurations() {
        for (npc in addNpcs) {
            registry.add(npc)
        }
        addNpcs.clear()
    }

    private fun Npc.shouldTrigger(triggerCycle: Int): Boolean = mapClock.cycle == triggerCycle
}
