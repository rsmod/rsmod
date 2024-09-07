package org.rsmod.api.repo.obj

import jakarta.inject.Inject
import java.util.LinkedList
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.game.MapClock
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.Obj
import org.rsmod.game.obj.ObjEntity
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
    private val addDurations = LinkedList<ObjAddDuration>()
    private val delDurations = LinkedList<ObjDelDuration>()

    public fun add(obj: Obj, duration: Int, reveal: Int = duration - DEFAULT_REVEAL_DELTA) {
        require(obj.count > 0) { "Obj must have a `count` higher than 0: $obj" }
        val register = registry.add(obj)
        when (register) {
            // TODO: Check how the duration is supposed to be calculated. Does it take the
            //  greater duration comparing the existing obj vs `duration` input from this call?
            is ObjRegistry.RegisterMerge -> updateDurations(register.merged, duration, reveal)
            is ObjRegistry.RegisterSplit -> addDurations(register.split, duration, reveal)
            ObjRegistry.RegisterStack -> addDuration(obj, duration, reveal)
        }
    }

    public fun add(
        type: ObjType,
        coords: CoordGrid,
        duration: Int,
        count: Int = 1,
        reveal: Int = DEFAULT_REVEAL_DELTA,
        receiverId: Long? = null,
    ): Obj {
        val obj =
            if (receiverId != null) {
                Obj(coords, type, count, mapClock.tick, receiverId)
            } else {
                Obj(coords, type, count, mapClock.tick)
            }
        add(obj, duration, reveal)
        return obj
    }

    public fun add(
        obj: InvObj,
        coords: CoordGrid,
        duration: Int,
        reveal: Int = DEFAULT_REVEAL_DELTA,
        receiverId: Long? = null,
    ): Obj {
        val obj =
            if (receiverId != null) {
                val entity = ObjEntity(obj.id, obj.count, ObjScope.Private.id)
                Obj(coords, entity, mapClock.tick, receiverId)
            } else {
                val entity = ObjEntity(obj.id, obj.count, ObjScope.Temp.id)
                Obj(coords, entity, mapClock.tick, Obj.NULL_RECEIVER_ID)
            }
        add(obj, duration, reveal)
        return obj
    }

    public fun del(obj: Obj, duration: Int = obj.respawnRate()): Boolean {
        val deleted = registry.del(obj)
        if (!deleted) {
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

    public fun findAll(coords: CoordGrid): Sequence<Obj> = registry.findAll(coords)

    public fun findAll(zone: ZoneKey): Sequence<Obj> = registry.findAll(zone)

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
            registry.add(duration.obj)
            iterator.remove()
        }
    }

    private fun processAddDurations() {
        val iterator = addDurations.iterator()
        while (iterator.hasNext()) {
            val duration = iterator.next()
            if (mapClock.tick == duration.revealCycle) {
                registry.reveal(duration.obj)
            }
            if (duration.shouldTrigger()) {
                registry.del(duration.obj)
                iterator.remove()
            }
        }
    }

    private fun ObjCycleDuration.shouldTrigger(): Boolean = mapClock >= triggerCycle

    private sealed class ObjCycleDuration(val obj: Obj, var triggerCycle: Int)

    private class ObjAddDuration(obj: Obj, triggerCycle: Int, var revealCycle: Int) :
        ObjCycleDuration(obj, triggerCycle)

    private class ObjDelDuration(obj: Obj, triggerCycle: Int) : ObjCycleDuration(obj, triggerCycle)

    public companion object {
        public const val DEFAULT_REVEAL_DELTA: Int = 100
    }
}
