package org.rsmod.api.repo.loc

import jakarta.inject.Inject
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.loc.LocRegistryResult
import org.rsmod.game.MapClock
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocShape
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.param.ParamType
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.pathfinder.loc.LocLayerConstants

public class LocRepository
@Inject
constructor(
    private val mapClock: MapClock,
    private val registry: LocRegistry,
    private val locTypes: LocTypeList,
) {
    private val addDurations = ArrayDeque<LocCycleDuration>()
    private val delDurations = ArrayDeque<LocCycleDuration>()

    public fun add(loc: LocInfo, duration: Int) {
        val add = registry.add(loc)
        if (add.shouldDespawn() && duration != Int.MAX_VALUE) {
            val revertCycle = mapClock + duration
            val locDuration = LocCycleDuration(loc, revertCycle)
            delDurations.removeExisting(loc)
            addDurations.removeExisting(loc)
            addDurations.add(locDuration)
        }
    }

    private fun LocRegistryResult.shouldDespawn(): Boolean = this == LocRegistryResult.AddSpawned

    public fun add(
        coords: CoordGrid,
        type: LocType,
        duration: Int,
        angle: LocAngle = LocAngle.West,
        shape: LocShape = LocShape.CentrepieceStraight,
    ): LocInfo {
        val layer = LocLayerConstants.of(shape.id)
        val entity = LocEntity(type.id, shape.id, angle.id)
        val loc = LocInfo(layer, coords, entity)
        add(loc, duration)
        return loc
    }

    public fun del(bound: BoundLocInfo, duration: Int): Boolean = del(bound.toLocInfo(), duration)

    public fun del(loc: LocInfo, duration: Int): Boolean {
        val delete = registry.del(loc)
        if (delete == LocRegistryResult.DeleteFailed) {
            return false
        }
        if (delete.canRespawn() && duration != Int.MAX_VALUE) {
            val revertCycle = mapClock + duration
            val locDuration = LocCycleDuration(loc, revertCycle)
            addDurations.removeExisting(loc)
            delDurations.removeExisting(loc)
            delDurations.add(locDuration)
        }
        return true
    }

    public fun change(from: BoundLocInfo, into: LocType, duration: Int): Unit =
        change(from.toLocInfo(), into, duration)

    public fun change(from: LocInfo, into: LocType, duration: Int) {
        val delete = del(from, duration)
        check(delete) { "Loc could not be deleted: $from" }
        add(from.coords, into, duration, from.angle, from.shape)
    }

    private fun LocRegistryResult.canRespawn(): Boolean = this == LocRegistryResult.DeleteMapLoc

    private fun BoundLocInfo.toLocInfo(): LocInfo = LocInfo(layer, coords, entity)

    private fun ArrayDeque<LocCycleDuration>.removeExisting(loc: LocInfo) {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.loc.coords == loc.coords && next.loc.entity == loc.entity) {
                iterator.remove()
                break
            }
        }
    }

    public fun findAll(zone: ZoneKey): Sequence<LocInfo> = registry.findAll(zone)

    public fun findExact(
        coords: CoordGrid,
        type: LocType? = null,
        shape: LocShape? = null,
        angle: LocAngle? = null,
    ): LocInfo? = registry.find(coords, type?.id, shape?.id, angle?.id)

    public fun findExact(
        coords: CoordGrid,
        content: ContentGroupType,
        type: LocType? = null,
        shape: LocShape? = null,
        angle: LocAngle? = null,
    ): LocInfo? {
        val loc = registry.find(coords, type?.id, shape?.id, angle?.id) ?: return null
        return if (locTypes[loc].contentGroup == content.id) {
            loc
        } else {
            null
        }
    }

    public fun <T : Any> locParam(loc: LocInfo, param: ParamType<T>): T? =
        locTypes[loc].paramOrNull(param)

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
            registry.add(duration.loc)
            iterator.remove()
        }
    }

    private fun processAddDurations() {
        val iterator = addDurations.iterator()
        while (iterator.hasNext()) {
            val duration = iterator.next()
            if (!duration.shouldTrigger()) {
                continue
            }
            registry.del(duration.loc)
            iterator.remove()
        }
    }

    private fun LocCycleDuration.shouldTrigger(): Boolean = mapClock >= triggerCycle

    private data class LocCycleDuration(val loc: LocInfo, val triggerCycle: Int)
}
