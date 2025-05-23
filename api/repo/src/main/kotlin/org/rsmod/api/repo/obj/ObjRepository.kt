package org.rsmod.api.repo.obj

import jakarta.inject.Inject
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.registry.obj.ObjRegistryResult
import org.rsmod.api.registry.obj.isSuccess
import org.rsmod.game.MapClock
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.Obj
import org.rsmod.game.obj.ObjScope
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

public class ObjRepository
@Inject
constructor(
    private val mapClock: MapClock,
    private val registry: ObjRegistry,
    private val objTypes: ObjTypeList,
) {
    private val addDurations = ArrayDeque<ObjAddDuration>()
    private val delDurations = ArrayDeque<ObjDelDuration>()
    private val addDelayed = ArrayDeque<ObjAddDelayed>()

    public fun add(obj: Obj, duration: Int, reveal: Int = DEFAULT_REVEAL_DELAY): Boolean {
        val register = register(obj, duration, reveal)
        return register.isSuccess()
    }

    private fun register(obj: Obj, duration: Int, reveal: Int): ObjRegistryResult.Add {
        require(obj.count > 0) { "Obj must have a `count` higher than 0: $obj" }
        val register = registry.add(obj)
        when (register) {
            // TODO: Check how the duration is supposed to be calculated. Does it take the
            //  greater duration comparing the existing obj vs `duration` input from this call?
            is ObjRegistryResult.Add.Merge -> updateDurations(register.merged, duration, reveal)
            is ObjRegistryResult.Add.Split -> addDurations(register.split, duration, reveal)
            is ObjRegistryResult.Add.Stack -> addDuration(obj, duration, reveal)
            is ObjRegistryResult.Add.BulkNonStackableLimitExceeded -> return register
            is ObjRegistryResult.Add.InvalidDummyitem -> {
                throw IllegalStateException("Dummyitem cannot be added to registry: $obj")
            }
        }
        return register
    }

    public fun add(
        type: ObjType,
        coords: CoordGrid,
        duration: Int,
        receiver: Player? = null,
        count: Int = 1,
        reveal: Int = DEFAULT_REVEAL_DELAY,
    ): Obj {
        val obj =
            if (receiver != null) {
                Obj.fromOwner(receiver, coords, type, count)
            } else {
                Obj.fromServer(mapClock, coords, type, count)
            }
        register(obj, duration, reveal)
        return obj
    }

    public fun add(
        invObj: InvObj,
        coords: CoordGrid,
        duration: Int,
        receiver: Player? = null,
        reveal: Int = DEFAULT_REVEAL_DELAY,
    ): Obj {
        val obj =
            if (receiver != null) {
                Obj.fromOwner(receiver, coords, invObj)
            } else {
                Obj.fromServer(mapClock, coords, invObj)
            }
        register(obj, duration, reveal)
        return obj
    }

    public fun del(obj: Obj, duration: Int = obj.respawnRate()): Boolean {
        val deleted = registry.del(obj)
        if (!deleted.isSuccess()) {
            return false
        }
        if (obj.canRespawn() && duration != Int.MAX_VALUE) {
            val respawnCycle = mapClock + duration
            val objDuration = ObjDelDuration(obj, respawnCycle)
            delDurations.add(objDuration)
        }
        return true
    }

    private fun Obj.respawnRate(): Int = objTypes[this].respawnRate

    private fun Obj.canRespawn(): Boolean = scope == ObjScope.Perm

    public fun addDelayed(obj: Obj, spawnDelay: Int, duration: Int) {
        val spawnCycle = mapClock + spawnDelay
        val objDuration = ObjAddDelayed(obj, spawnCycle, duration = duration)
        addDelayed.add(objDuration)
    }

    public fun findAll(zone: ZoneKey): Sequence<Obj> = registry.findAll(zone)

    public fun findAll(coords: CoordGrid): Sequence<Obj> = registry.findAll(coords)

    private fun addDurations(objs: Iterable<Obj>, duration: Int, reveal: Int) {
        for (obj in objs) {
            addDuration(obj, duration, reveal)
        }
    }

    private fun addDuration(obj: Obj, duration: Int, reveal: Int) {
        if (duration == Int.MAX_VALUE) {
            return
        }
        val deleteCycle = mapClock + duration
        val revealCycle = mapClock + reveal
        val objDuration = ObjAddDuration(obj, deleteCycle, revealCycle)
        addDurations.add(objDuration)
    }

    private fun updateDurations(obj: Obj, newDuration: Int, newReveal: Int) {
        if (newDuration == Int.MAX_VALUE) {
            return
        }
        val oldDuration = addDurations.firstOrNull { it.obj === obj } ?: return
        oldDuration.triggerCycle = mapClock + newDuration
        oldDuration.revealCycle = mapClock + newReveal
    }

    internal fun processDurations() {
        if (delDurations.isNotEmpty()) {
            processDelDurations()
        }
        if (addDurations.isNotEmpty()) {
            processAddDurations()
        }
    }

    private fun processDelDurations() {
        val iterator = delDurations.iterator()
        while (iterator.hasNext()) {
            val duration = iterator.next()
            if (!duration.shouldTrigger()) {
                continue
            }
            val result = registry.add(duration.obj)
            check(result.isSuccess()) { "Failed to respawn obj: $duration" }
            iterator.remove()
        }
    }

    private fun processAddDurations() {
        val iterator = addDurations.iterator()
        while (iterator.hasNext()) {
            val duration = iterator.next()
            if (mapClock.cycle == duration.revealCycle) {
                registry.reveal(duration.obj)
            }
            if (duration.shouldTrigger()) {
                registry.del(duration.obj)
                iterator.remove()
            }
        }
    }

    internal fun processDelayedAdd() {
        if (addDelayed.isNotEmpty()) {
            processAddDelayed()
        }
    }

    private fun processAddDelayed() {
        val iterator = addDelayed.iterator()
        while (iterator.hasNext()) {
            val duration = iterator.next()
            if (duration.shouldTrigger()) {
                add(duration.obj, duration = duration.duration)
                iterator.remove()
            }
        }
    }

    private fun ObjCycleDuration.shouldTrigger(): Boolean = mapClock >= triggerCycle

    private sealed class ObjCycleDuration(val obj: Obj, var triggerCycle: Int)

    private class ObjAddDuration(obj: Obj, triggerCycle: Int, var revealCycle: Int) :
        ObjCycleDuration(obj, triggerCycle)

    private class ObjDelDuration(obj: Obj, triggerCycle: Int) : ObjCycleDuration(obj, triggerCycle)

    private class ObjAddDelayed(obj: Obj, triggerCycle: Int, val duration: Int) :
        ObjCycleDuration(obj, triggerCycle)

    public companion object {
        public const val DEFAULT_REVEAL_DELAY: Int = 100
    }
}
