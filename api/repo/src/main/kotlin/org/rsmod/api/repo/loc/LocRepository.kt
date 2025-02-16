package org.rsmod.api.repo.loc

import jakarta.inject.Inject
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.loc.LocRegistryResult
import org.rsmod.api.registry.loc.isSuccess
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.game.MapClock
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocShape
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.loc.LocLayerConstants

public class LocRepository
@Inject
constructor(
    private val mapClock: MapClock,
    private val locTypes: LocTypeList,
    private val locReg: LocRegistry,
    private val regionReg: RegionRegistry,
) {
    private val addDurations = ArrayDeque<LocCycleDuration>()
    private val delDurations = ArrayDeque<LocCycleDuration>()

    public fun add(loc: LocInfo, duration: Int): Boolean {
        val add = locReg.add(loc)

        if (!add.isSuccess()) {
            return false
        }

        if (add.shouldDespawn() && duration != Int.MAX_VALUE) {
            val revertCycle = mapClock + duration
            val validator = add.regionValidator()
            val locDuration = LocCycleDuration(loc, revertCycle, validator)
            delDurations.removeExisting(loc)
            addDurations.removeExisting(loc)
            addDurations.add(locDuration)
        }

        return true
    }

    public fun add(
        coords: CoordGrid,
        type: LocType,
        duration: Int,
        angle: LocAngle,
        shape: LocShape,
    ): LocInfo {
        val layer = LocLayerConstants.of(shape.id)
        val entity = LocEntity(type.id, shape.id, angle.id)
        val loc = LocInfo(layer, coords, entity)
        add(loc, duration)
        return loc
    }

    public fun del(loc: LocInfo, duration: Int): Boolean {
        val delete = locReg.del(loc)

        if (!delete.isSuccess()) {
            return false
        }

        if (delete.canRespawn() && duration != Int.MAX_VALUE) {
            val revertCycle = mapClock + duration
            val validator = delete.regionValidator()
            val locDuration = LocCycleDuration(loc, revertCycle, validator)
            addDurations.removeExisting(loc)
            delDurations.removeExisting(loc)
            delDurations.add(locDuration)
        }

        return true
    }

    public fun del(bound: BoundLocInfo, duration: Int): Boolean {
        val loc = LocInfo(bound.layer, bound.coords, bound.entity)
        return del(loc, duration)
    }

    public fun change(from: LocInfo, into: LocType, duration: Int) {
        add(from.coords, into, duration, from.angle, from.shape)
    }

    public fun change(from: BoundLocInfo, into: LocType, duration: Int) {
        add(from.coords, into, duration, from.angle, from.shape)
    }

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

    public fun findAll(zone: ZoneKey): Sequence<LocInfo> = locReg.findAll(zone)

    public fun findAll(coords: CoordGrid): Sequence<LocInfo> =
        findAll(ZoneKey.from(coords)).filter { it.coords == coords }

    public fun findExact(coords: CoordGrid, type: LocType): LocInfo? =
        locReg.findType(coords, type.id)

    public fun findExact(coords: CoordGrid, shape: LocShape): LocInfo? =
        locReg.findShape(coords, shape.id)

    public fun findExact(coords: CoordGrid, content: ContentGroupType, shape: LocShape): LocInfo? {
        val loc = locReg.findShape(coords, shape.id) ?: return null
        return loc.takeIf { locTypes[it].contentGroup == content.id }
    }

    public fun findExact(coords: CoordGrid, content: ContentGroupType, type: LocType): LocInfo? {
        val loc = locReg.findType(coords, type.id) ?: return null
        return loc.takeIf { locTypes[it].contentGroup == content.id }
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
            if (duration.isValid()) {
                locReg.add(duration.loc)
            }
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
            if (duration.isValid()) {
                locReg.del(duration.loc)
            }
            iterator.remove()
        }
    }

    private fun LocCycleDuration.shouldTrigger(): Boolean = mapClock >= triggerCycle

    private fun LocCycleDuration.isValid(): Boolean {
        val validator = regionValidator ?: return true
        val (slot, uid) = validator
        return regionReg.isValid(slot, uid)
    }

    private data class LocCycleDuration(
        val loc: LocInfo,
        val triggerCycle: Int,
        val regionValidator: RegionValidator?,
    )

    private data class RegionValidator(val slot: Int, val uid: Int)

    private companion object {
        private fun LocRegistryResult.Add.Success.shouldDespawn(): Boolean =
            this is LocRegistryResult.Add.NormalSpawned ||
                this is LocRegistryResult.Add.RegionSpawned

        private fun LocRegistryResult.Delete.Success.canRespawn(): Boolean =
            this is LocRegistryResult.Delete.NormalMapLoc ||
                this is LocRegistryResult.Delete.RegionMapLoc

        private fun LocRegistryResult.Add.Success.regionValidator(): RegionValidator? =
            if (this is LocRegistryResult.Add.RegionSuccess) {
                RegionValidator(regionSlot, regionUid)
            } else {
                null
            }

        private fun LocRegistryResult.Delete.Success.regionValidator(): RegionValidator? =
            if (this is LocRegistryResult.Delete.RegionSuccess) {
                RegionValidator(regionSlot, regionUid)
            } else {
                null
            }
    }
}
