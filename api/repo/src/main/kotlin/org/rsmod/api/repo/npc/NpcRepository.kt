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
        registry.add(npc)
        if (duration != Int.MAX_VALUE) {
            val deleteCycle = mapClock + duration
            npc.lifecycleDelCycle = deleteCycle
        }
        return true
    }

    public fun del(npc: Npc, duration: Int): Boolean {
        registry.del(npc)
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
        if (shouldTrigger(npc.lifecycleRevealCycle)) {
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
            if (shouldTrigger(npc.lifecycleDelCycle)) {
                delNpcs.add(npc)
            }
            if (shouldTrigger(npc.lifecycleAddCycle)) {
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

    private fun shouldTrigger(triggerCycle: Int): Boolean = mapClock.cycle == triggerCycle
}
