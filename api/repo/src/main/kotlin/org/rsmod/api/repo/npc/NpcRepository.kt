package org.rsmod.api.repo.npc

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import jakarta.inject.Inject
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.registry.npc.isSuccess
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

public class NpcRepository
@Inject
constructor(
    private val mapClock: MapClock,
    private val registry: NpcRegistry,
    private val npcList: NpcList,
) {
    private val addNpcs = ObjectArrayList<Npc>()
    private val delNpcs = ObjectArrayList<Npc>()

    /**
     * **Note:** If [duration] is equal to [Int.MAX_VALUE], the [npc] will have its `respawn` flag
     * set to `true` and will respawn on death.
     */
    public fun add(npc: Npc, duration: Int) {
        val add = registry.add(npc)
        check(add.isSuccess()) { "Failed to add npc. (result=$add, npc=$npc)" }

        val permanentSpawn = duration == Int.MAX_VALUE
        npc.respawns = permanentSpawn

        if (!permanentSpawn) {
            val deleteCycle = mapClock + duration
            npc.lifecycleDelCycle = deleteCycle
        }
    }

    /**
     * **Note:** This function will implicitly call [Npc.destroy] which will cancel any ongoing
     * coroutine attached to the [npc].
     *
     * @see [Npc.activeCoroutine]
     */
    public fun del(npc: Npc, duration: Int) {
        val del = registry.del(npc)
        check(del.isSuccess()) { "Failed to delete npc. (result=$del, npc=$npc)" }
        if (duration != Int.MAX_VALUE) {
            val addCycle = mapClock + duration
            npc.lifecycleAddCycle = addCycle
        }
    }

    /** **Note:** Unlike [del], this function does **not** call [Npc.cancelActiveCoroutine]. */
    public fun hide(npc: Npc, duration: Int) {
        registry.hide(npc)
        if (duration != Int.MAX_VALUE) {
            val revealCycle = mapClock + duration
            npc.lifecycleRevealCycle = revealCycle
        }
    }

    public fun findAll(zone: ZoneKey): Sequence<Npc> = registry.findAll(zone)

    public fun findAll(coords: CoordGrid): Sequence<Npc> =
        findAll(ZoneKey.from(coords)).filter { it.coords == coords }

    public fun findAll(zone: ZoneKey, zoneRadius: Int): Sequence<Npc> {
        return sequence {
            for (x in -zoneRadius..zoneRadius) {
                for (z in -zoneRadius..zoneRadius) {
                    val translate = zone.translate(x, z)
                    val players = findAll(translate)
                    yieldAll(players)
                }
            }
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
