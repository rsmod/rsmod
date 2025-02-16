package org.rsmod.api.registry.loc

import jakarta.inject.Inject
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocZoneKey
import org.rsmod.game.map.LocZoneStorage
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

public class LocRegistry
@Inject
constructor(private val locZones: LocZoneStorage, private val normalReg: LocRegistryNormal) {
    public fun add(loc: LocInfo): LocRegistryResult.Add =
        if (inRegionArea(loc.coords)) {
            normalReg.add(loc)
        } else {
            normalReg.add(loc)
        }

    public fun del(loc: LocInfo): LocRegistryResult.Delete =
        if (inRegionArea(loc.coords)) {
            normalReg.del(loc)
        } else {
            normalReg.del(loc)
        }

    public fun findType(coords: CoordGrid, loc: Int): LocInfo? =
        if (inRegionArea(coords)) {
            normalReg.findType(coords, loc)
        } else {
            normalReg.findType(coords, loc)
        }

    public fun findShape(coords: CoordGrid, shape: Int): LocInfo? =
        if (inRegionArea(coords)) {
            normalReg.findShape(coords, shape)
        } else {
            normalReg.findShape(coords, shape)
        }

    public fun findAll(zone: ZoneKey): Sequence<LocInfo> =
        if (inRegionArea(zone)) {
            normalReg.findAll(zone)
        } else {
            normalReg.findAll(zone)
        }

    /**
     * Finds all _dynamically_ spawned location entities within the given [zone].
     *
     * This function returns a sequence of all `LocInfo` locs that have been dynamically added
     * during runtime and does not consider static locs loaded from the game map during start-up.
     *
     * **Usage Scope:** The use of `findAllSpawned` is limited and should be invoked only in very
     * specific scenarios. Currently, this function is designed to fetch dynamically spawned locs
     * for the purpose of synchronizing them with a player's client when the corresponding zone
     * becomes "visible" in the player's viewable area. This is essential for ensuring that any
     * dynamically added locs are correctly rendered for the player.
     *
     * **Important:** For general use cases, including queries that involve both static and
     * dynamically spawned locs, prefer using [findAll]. `findAll` provides a comprehensive view of
     * all locs in a zone, respecting the priority of dynamically spawned locs over static ones when
     * there are conflicts.
     */
    public fun findAllSpawned(zone: ZoneKey): Sequence<LocInfo> {
        val entries = locZones.spawnedLocs[zone]?.byte2IntEntrySet() ?: return emptySequence()
        return sequence {
            val zoneCoords = zone.toCoords()
            for (entry in entries) {
                val entity = LocEntity(entry.intValue)
                val key = LocZoneKey(entry.byteKey)
                val coords = zoneCoords.translate(key.x, key.z)
                val loc = LocInfo(key.layer, coords, entity)
                yield(loc)
            }
        }
    }

    public fun isValid(coords: CoordGrid, loc: Int): Boolean {
        val found = findType(coords, loc) ?: return false
        return found.entity.id != DELETED_LOC_ID
    }

    public companion object {
        /**
         * Constant used as a reference to a "deleted" loc entity id. This is used when map-loaded
         * (spawned from cache) locs are "deleted" via [del] functions.
         */
        public val DELETED_LOC_ID: Int = LocEntity.NULL.id

        private fun inRegionArea(zone: ZoneKey): Boolean = false

        private fun inRegionArea(coords: CoordGrid): Boolean = false
    }
}
