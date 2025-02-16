package org.rsmod.api.registry.loc

import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet
import jakarta.inject.Inject
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocZoneKey
import org.rsmod.game.map.LocZoneStorage
import org.rsmod.game.map.ZoneLocMap
import org.rsmod.game.map.collision.addLoc
import org.rsmod.game.map.collision.removeLoc
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.collision.CollisionFlagMap

public class LocRegistryNormal
@Inject
constructor(
    private val updates: ZoneUpdateMap,
    private val collision: CollisionFlagMap,
    private val locTypes: LocTypeList,
    private val locZones: LocZoneStorage,
) {
    private val mapLocs: ZoneLocMap by locZones::mapLocs
    private val spawnedLocs: ZoneLocMap by locZones::spawnedLocs

    public fun add(loc: LocInfo): LocRegistryResult.Add {
        check(!RegionRegistry.inWorkingArea(loc.coords)) {
            "Cannot add loc to region-reserved area with `LocRegistryNormal`."
        }

        val zoneKey = ZoneKey.from(loc.coords)
        val locZoneKey = loc.toLocZoneGridKey()
        val spawnZone = spawnedLocs.getOrPut(zoneKey)
        val mapLoc = mapLocs[zoneKey]?.get(locZoneKey.packed)
        val exactMapLocExists = loc.entity.packed == mapLoc

        removeSpawnedLoc(spawnZone, locZoneKey, loc.coords)

        if (!exactMapLocExists) {
            spawnZone[locZoneKey.packed] = loc.entity.packed
        }

        addLocCollision(loc)
        updates.locAdd(loc)

        return if (exactMapLocExists) {
            LocRegistryResult.Add.SpawnedMapLoc
        } else {
            LocRegistryResult.Add.SpawnedDynamic
        }
    }

    public fun del(loc: LocInfo): LocRegistryResult.Delete {
        val zoneKey = ZoneKey.from(loc.coords)
        val locZoneKey = loc.toLocZoneGridKey()

        // Deleting spawned locs takes priority over static map locs.
        val deletedSpawnedLoc = deleteSpawnedLoc(zoneKey, locZoneKey, loc)
        if (deletedSpawnedLoc) {
            return LocRegistryResult.Delete.RemovedDynamic
        }

        val deletedMapLoc = deleteStaticLoc(zoneKey, locZoneKey, loc)
        if (deletedMapLoc) {
            return LocRegistryResult.Delete.RemovedMapLoc
        }

        return LocRegistryResult.Delete.LocNotFound
    }

    public fun findType(coords: CoordGrid, loc: Int): LocInfo? {
        val spawnedLoc = spawnedLocs.findType(coords, loc)
        if (spawnedLoc != null) {
            return spawnedLoc.takeIf { it.entity.id != DELETED_LOC_ID }
        }

        val mapLoc = mapLocs.findType(coords, loc) ?: return null
        // Map locs can sometimes be replaced by a spawned loc that occupies the same "layer."
        // Take for example trees being replaced by tree stumps during woodcutting; these will
        // share shape and angle, but are different locs. Without this condition, the original
        // tree would be found in `mapLocs` and would otherwise return as a valid `LocInfo.`
        val layeredLoc = spawnedLocs.findLayer(coords, mapLoc.layer)

        return mapLoc.takeIf { layeredLoc == null }
    }

    public fun findShape(coords: CoordGrid, shape: Int): LocInfo? {
        val spawnedLoc = spawnedLocs.findShape(coords, shape)
        if (spawnedLoc != null) {
            return spawnedLoc.takeIf { it.entity.id != DELETED_LOC_ID }
        }

        val mapLoc = mapLocs.findShape(coords, shape) ?: return null
        // Map locs can sometimes be replaced by a spawned loc that occupies the same "layer."
        // Take for example trees being replaced by tree stumps during woodcutting; these will
        // share shape and angle, but are different locs. Without this condition, the original
        // tree would be found in `mapLocs` and would otherwise return as a valid `LocInfo.`
        val layeredLoc = spawnedLocs.findLayer(coords, mapLoc.layer)

        return mapLoc.takeIf { layeredLoc == null }
    }

    public fun findAll(zone: ZoneKey): Sequence<LocInfo> {
        val spawnedLocs = spawnedLocs[zone]?.byte2IntEntrySet()
        val staticLocs = mapLocs[zone]?.byte2IntEntrySet()
        if (spawnedLocs == null && staticLocs == null) {
            return emptySequence()
        }
        return sequence {
            val processedKeys = ByteOpenHashSet()
            val zoneCoords = zone.toCoords()
            if (spawnedLocs != null) {
                for (entry in spawnedLocs) {
                    // Add the byteKey of each entry to the processedKeys set. This ensures that if
                    // a loc (location entity) exists in both spawnedLocs and staticLocs for the
                    // same zone grid (tile within the 7x7 zone) and their given `layer`, the loc
                    // from spawnedLocs takes priority.
                    processedKeys += entry.byteKey
                    val entity = LocEntity(entry.intValue)
                    if (entity.id == DELETED_LOC_ID) {
                        continue
                    }
                    val key = LocZoneKey(entry.byteKey)
                    val coords = zoneCoords.translate(key.x, key.z)
                    val loc = LocInfo(key.layer, coords, entity)
                    yield(loc)
                }
            }
            if (staticLocs != null) {
                for (entry in staticLocs) {
                    // Check if the entry's byteKey has already been processed by spawnedLocs.
                    // If it has, skip this entry to ensure that any loc from spawnedLocs takes
                    // priority over staticLocs for the same zone grid and layer.
                    if (entry.byteKey in processedKeys) {
                        continue
                    }
                    val entity = LocEntity(entry.intValue)
                    val key = LocZoneKey(entry.byteKey)
                    val coords = zoneCoords.translate(key.x, key.z)
                    val loc = LocInfo(key.layer, coords, entity)
                    yield(loc)
                }
            }
        }
    }

    /**
     * Removes a spawned loc entity from the [zone] map and removes its collision data.
     *
     * This function checks if a location entity associated with the specified [key] exists in the
     * [zone] map. If found, it removes the entity from the map and any associated collision data
     * using [removeLocCollision].
     *
     * _Note that this operation does not trigger an update within [updates]._
     */
    private fun removeSpawnedLoc(zone: Byte2IntOpenHashMap, key: LocZoneKey, coords: CoordGrid) {
        val removed = zone.remove(key.packed)
        if (removed != zone.defaultReturnValue()) {
            val entity = LocEntity(removed)
            val removeLoc = LocInfo(key.layer, coords, entity)
            removeLocCollision(removeLoc)
        }
    }

    /**
     * Deletes a spawned loc entity from the [spawnedLocs] map using the given [zoneKey] and updates
     * its respective viewable zone.
     *
     * This function checks if a loc entity exists at the specified [zoneKey] and [locZoneKey]. If
     * found, it removes the entity, its collision data via [removeLocCollision], _and unlike
     * [removeSpawnedLoc] function, will trigger an update within [updates]_.
     *
     * Additionally, if a static/map loc was previously masked by the "deleted" loc, this function
     * will reapply its collision data as well as trigger a "loc add" update within [updates].
     *
     * @return `true` if the loc entity was removed; `false` otherwise.
     * @see [removeSpawnedLoc]
     */
    private fun deleteSpawnedLoc(zoneKey: ZoneKey, locZoneKey: LocZoneKey, loc: LocInfo): Boolean {
        val zone = spawnedLocs[zoneKey] ?: return false
        val previousLoc = zone.get(locZoneKey.packed)
        if (previousLoc == zone.defaultReturnValue()) {
            return false
        }

        // This makes sure that the [loc] given as input matches the same id, shape, and angle as
        // the loc found in the given zone grid with the same loc layer.
        // This acts as a safeguard against trying to delete a loc info that may share the same
        // layer and zone grid as the found loc, but does not share other metadata such as its
        // type, which could lead to incorrect data being used for i.e., removing its collision.
        if (loc.entity.packed != previousLoc) {
            return false
        }

        val removed = zone.remove(locZoneKey.packed)
        if (removed == zone.defaultReturnValue()) {
            return false
        }

        removeLocCollision(loc)

        val maskedStaticLoc = mapLocs[zoneKey]?.getOrDefault(locZoneKey.packed, null)
        if (maskedStaticLoc != null) {
            val maskedLoc = LocInfo(loc.layer, loc.coords, LocEntity(maskedStaticLoc))
            addLocCollision(maskedLoc)
            updates.locAdd(maskedLoc)
        } else {
            updates.locDel(loc)
        }

        return true
    }

    /**
     * Marks a static location as deleted based on the given [zoneKey] and [locZoneKey].
     *
     * This function does not remove the loc entry from [mapLocs]; instead, it replaces the entry
     * with a special "deleted loc" entity (using [DELETED_LOC_ID]). This ensures:
     * - The deleted loc is ignored in [findAll] and similar functions.
     * - A "loc del" zone update is sent when players enter the affected zone.
     *
     * @return `true` if a static map loc was marked as deleted, `false` otherwise.
     */
    private fun deleteStaticLoc(zoneKey: ZoneKey, locZoneKey: LocZoneKey, loc: LocInfo): Boolean {
        val staticZone = mapLocs[zoneKey] ?: return false
        val staticLoc = staticZone.getOrDefault(locZoneKey.packed, null) ?: return false

        // This makes sure that the [loc] given as input matches the same id, shape, and angle as
        // the loc found in the given zone grid with the same loc layer.
        // This acts as a safeguard against trying to delete a loc info that may share the same
        // layer and zone grid as the found loc, but does not share other metadata such as its
        // type, which could lead to incorrect data being used for i.e., removing its collision.
        if (loc.entity.packed != staticLoc) {
            return false
        }

        val deletedLoc = LocEntity(staticLoc).copy(id = DELETED_LOC_ID)
        spawnedLocs[zoneKey, locZoneKey] = deletedLoc
        removeLocCollision(loc)
        updates.locDel(loc)

        return true
    }

    private fun addLocCollision(loc: LocInfo) {
        val type = locTypes[loc.id] ?: return
        collision.addLoc(loc, type)
    }

    private fun removeLocCollision(loc: LocInfo) {
        val type = locTypes[loc.id] ?: return
        collision.removeLoc(loc, type)
    }

    public companion object {
        /**
         * Constant used as a reference to a "deleted" loc entity id. This is used when map-loaded
         * (spawned from cache) locs are "deleted" via [del] functions.
         */
        public val DELETED_LOC_ID: Int = LocEntity.NULL.id
    }
}
