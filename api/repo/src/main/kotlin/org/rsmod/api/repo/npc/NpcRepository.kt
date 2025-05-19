package org.rsmod.api.repo.npc

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import jakarta.inject.Inject
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.registry.npc.isSuccess
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.npc.NpcStateEvents
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
    private val addDelayedNpcs = ArrayDeque<Npc>()

    /**
     * **Note**: If [duration] is equal to [Int.MAX_VALUE], the [npc] will have its `respawn` flag
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
     * **Note**: This function will implicitly call [Npc.destroy] which will cancel any ongoing
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

    /** **Note**: Unlike [del], this function does **not** call [Npc.cancelActiveCoroutine]. */
    public fun hide(npc: Npc, duration: Int) {
        check(mapClock > npc.lifecycleRespawnCycle) {
            "Cannot hide npc while it is respawning. (npc=$npc)"
        }

        registry.hide(npc)
        if (duration != Int.MAX_VALUE) {
            val revealCycle = mapClock + duration
            npc.lifecycleRevealCycle = revealCycle
        }
    }

    /**
     * Similar to [hide], but this function is specifically for handling npc deaths.
     *
     * Calling this function will eventually trigger [NpcRegistry.respawn], which:
     * - Publishes the [NpcStateEvents.Respawn] event.
     * - Resets the npc's [Npc.coords] to its original [Npc.spawnCoords].
     *
     * **Note**: Unlike [del], this function does **not** call [Npc.cancelActiveCoroutine].
     */
    public fun despawn(npc: Npc, duration: Int) {
        // We do not want previously queued `hide` operations to go through in the
        // middle of the respawn.
        npc.lifecycleRevealCycle = 0

        registry.despawn(npc)
        if (duration != Int.MAX_VALUE) {
            val revealCycle = mapClock + duration
            npc.lifecycleRespawnCycle = revealCycle
        }
    }

    /**
     * Schedules the npc to spawn after a specified delay.
     *
     * This function is inspired by a similar system used for obj spawns. Its primary use case is to
     * delay the initial npc spawns during startup to ensure that scripts like `onNpcSpawn` are
     * present for npcs that are spawned early during the map-loading phase of cache loading.
     *
     * _**Note**: It is not confirmed that this exact system exists in the original game. Content
     * should avoid calling this function until such a system's existence and usage in the original
     * game are suspected or verified._
     *
     * @param spawnDelay The number of cycles to wait before spawning the npc.
     * @param duration The duration that the npc should remain active after spawning. Set to
     *   [Int.MAX_VALUE] for permanent spawn.
     * @throws IllegalStateException if the npc coords are within a region (instance).
     */
    public fun addDelayed(npc: Npc, spawnDelay: Int, duration: Int) {
        check(!RegionRegistry.inWorkingArea(npc.coords)) {
            "Cannot schedule npc spawn with `addDelayed` in regions."
        }
        val spawnCycle = mapClock + spawnDelay
        npc.lifecycleDelayedAddCycle = spawnCycle
        npc.lifecycleDelayedAddDuration = duration
        addDelayedNpcs.add(npc)
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
        if (shouldTrigger(npc.lifecycleRespawnCycle)) {
            registry.respawn(npc)
        } else if (shouldTrigger(npc.lifecycleRevealCycle)) {
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
            if (shouldTrigger(npc.lifecycleChangeCycle)) {
                npc.resetTransmog()
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

    internal fun processDelayedAdd() {
        if (addDelayedNpcs.isNotEmpty()) {
            processAddDelayed()
        }
    }

    private fun processAddDelayed() {
        val iterator = addDelayedNpcs.iterator()
        while (iterator.hasNext()) {
            val npc = iterator.next()
            if (shouldTrigger(npc.lifecycleDelayedAddCycle)) {
                add(npc, duration = npc.lifecycleDelayedAddDuration)
                iterator.remove()
            }
        }
    }

    private fun shouldTrigger(triggerCycle: Int): Boolean = mapClock.cycle == triggerCycle
}
