package org.rsmod.api.registry.loc

import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet
import jakarta.inject.Inject
import kotlin.collections.getValue
import kotlin.collections.plusAssign
import kotlin.collections.set
import kotlin.sequences.firstOrNull
import kotlin.takeIf
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocZoneKey
import org.rsmod.game.map.LocZoneStorage
import org.rsmod.game.map.ZoneLocMap
import org.rsmod.game.map.collision.addLoc
import org.rsmod.game.map.collision.removeLoc
import org.rsmod.game.region.Region
import org.rsmod.game.region.util.RegionRotations
import org.rsmod.game.region.zone.RegionZoneCopy
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.util.Rotations

public class LocRegistryRegion
@Inject
constructor(
    private val updates: ZoneUpdateMap,
    private val collision: CollisionFlagMap,
    private val locTypes: LocTypeList,
    private val locZones: LocZoneStorage,
    private val regions: RegionRegistry,
) {
    private val mapLocs: ZoneLocMap by locZones::mapLocs
    private val spawnedLocs: ZoneLocMap by locZones::spawnedLocs

    public fun add(loc: LocInfo): LocRegistryResult.Add {
        val region = regions[loc.coords] ?: return LocRegistryResult.Add.RegionNotRegistered
        val regionZone = ZoneKey.from(loc.coords)
        val copiedZone = regions[regionZone]

        // Reject loc operations in zones that fall outside the region's workable area.
        if (copiedZone == RegionZoneCopy.NULL) {
            return LocRegistryResult.Add.RegionZoneNotRegistered
        }

        val regionLocKey = loc.toLocZoneGridKey()
        val spawnZone = spawnedLocs.getOrPut(regionZone)
        val exactMapLocExists = mapLocExists(loc, region, copiedZone)

        removeSpawnedLoc(spawnZone, regionLocKey, loc.coords)

        if (!exactMapLocExists) {
            spawnZone[regionLocKey.packed] = loc.entity.packed
        }

        addLocCollision(loc)
        updates.locAdd(loc)
        return LocRegistryResult.Add.RegionSpawned(region)
    }

    public fun del(loc: LocInfo): LocRegistryResult.Delete {
        val region = regions[loc.coords] ?: return LocRegistryResult.Delete.RegionNotRegistered
        val regionZone = ZoneKey.from(loc.coords)
        val copiedZone = regions[regionZone]

        // Reject loc operations in zones that fall outside the region's workable area.
        if (copiedZone == RegionZoneCopy.NULL) {
            return LocRegistryResult.Delete.RegionZoneNotRegistered
        }

        val regionLocKey = loc.toLocZoneGridKey()

        // If the loc was cached and flagged as "remapped", we take priority in looking for the
        // normal loc based on its remapped coordinates.
        val remappedCoords = region.remappedLocCoords(loc.coords)
        if (remappedCoords != null) {
            val deleteRemappedLoc =
                deleteRemappedLoc(loc, region, regionZone, regionLocKey, copiedZone, remappedCoords)
            if (deleteRemappedLoc != null) {
                return deleteRemappedLoc
            }
        }

        // We resolve the "normal" loc by reverting the rotation and translation that was applied
        // during the region building stage and using the parameters for lookups.
        val (normalZone, normalLocKey) = resolveNormalMapping(loc, copiedZone)
        val rotation = copiedZone.rotation

        val deletedSpawnedLoc =
            deleteSpawnedLoc(regionZone, regionLocKey, normalZone, normalLocKey, rotation, loc)
        if (deletedSpawnedLoc) {
            return LocRegistryResult.Delete.RegionSpawned(region)
        }

        val deletedMapLoc = deleteStaticLoc(regionZone, regionLocKey, normalZone, normalLocKey, loc)
        if (deletedMapLoc) {
            return LocRegistryResult.Delete.RegionMapLoc(region)
        }

        return LocRegistryResult.Delete.LocNotFound
    }

    private fun deleteRemappedLoc(
        loc: LocInfo,
        region: Region,
        regionZone: ZoneKey,
        regionLocKey: LocZoneKey,
        copiedZone: RegionZoneCopy,
        remappedCoords: CoordGrid,
    ): LocRegistryResult.Delete? {
        val remappedZone = ZoneKey.from(remappedCoords)
        val remappedGrid = ZoneGrid.from(remappedCoords)
        val remappedLocKey = LocZoneKey(remappedGrid, loc.layer)
        val rotation = copiedZone.rotation

        val deletedSpawnedLoc =
            deleteSpawnedLoc(regionZone, regionLocKey, remappedZone, remappedLocKey, rotation, loc)
        if (deletedSpawnedLoc) {
            return LocRegistryResult.Delete.RegionSpawned(region)
        }

        val deletedMapLoc =
            deleteStaticLoc(regionZone, regionLocKey, remappedZone, remappedLocKey, loc)
        if (deletedMapLoc) {
            return LocRegistryResult.Delete.RegionMapLoc(region)
        }

        return null
    }

    public fun findType(coords: CoordGrid, loc: Int): LocInfo? {
        val spawnedLoc = spawnedLocs.findType(coords, loc)
        if (spawnedLoc != null) {
            return spawnedLoc.takeIf { it.entity.id != DELETED_LOC_ID }
        }

        val regionZone = ZoneKey.from(coords)
        val copiedZone = regions[regionZone]

        // If the zone is not associated to a copied region zone, we ignore the request. This can
        // occur when a zone rotates a loc out of its original zone, and ends up in a zone that the
        // region does not use. In cases where this loc can transform, (e.g., a tree which can be
        // cut down into a stump) we do not want it to operate in an invalid zone. This is why we
        // do not try and resolve the loc info and instead return `null`.
        if (copiedZone == RegionZoneCopy.NULL) {
            return null
        }

        // As we have limited information available (only the loc id), we have to try and "predict"
        // where the loc is placed in its `normal` zone. This is done by trying to find it by
        // inverting its rotation with both combinations: width x length and length x width.
        // This is what `findRotatedMapLoc` does in order to try and bruteforce the possible coords
        // within the normal zone.
        val rotatedMapLoc = findRotatedMapLoc(coords, copiedZone, loc)
        if (rotatedMapLoc != null) {
            // We have to apply the proper angle rotation to the original loc to return valid loc
            // metadata to the caller.
            val rotatedAngle = (rotatedMapLoc.angleId + copiedZone.rotation) and ANGLE_BIT_MASK
            val locEntity = rotatedMapLoc.entity.copy(angle = rotatedAngle)

            return rotatedMapLoc.copy(entity = locEntity)
        }

        val region = regions[coords] ?: return null

        // As a workaround for locs that have been translated away from their expected region zone,
        // we store these specialized loc parameters in their associated `Region`.
        val remappedCoords = region.remappedLocCoords(coords) ?: return null

        val remappedLookup = mapLocs.findType(remappedCoords, loc)
        if (remappedLookup != null) {
            val layeredLoc = spawnedLocs.findLayer(coords, remappedLookup.layer)
            val remappedLoc = remappedLookup.takeIf { layeredLoc == null } ?: return null

            val rotatedAngle = (remappedLookup.angleId + copiedZone.rotation) and ANGLE_BIT_MASK
            val locEntity = remappedLookup.entity.copy(angle = rotatedAngle)

            return remappedLoc.copy(coords = coords, entity = locEntity)
        }

        return null
    }

    private fun findRotatedMapLoc(
        coords: CoordGrid,
        copiedZone: RegionZoneCopy,
        loc: Int,
    ): LocInfo? {
        val normalZone = copiedZone.normalZone()
        val normalBase = normalZone.toCoords()
        val regionGrid = ZoneGrid.from(coords)

        val rotation = copiedZone.inverseRotation

        val type = locTypes.getValue(loc)
        val width = type.width
        val length = type.length

        val mapLoc1 = findRotatedMapLoc(regionGrid, normalBase, rotation, width, length, loc)
        if (mapLoc1 != null) {
            // Map locs can sometimes be replaced by a spawned loc that occupies the same "layer."
            // Take for example trees being replaced by tree stumps during woodcutting; these will
            // share shape and angle, but are different locs. Without this condition, the original
            // tree would be found in `mapLocs` and would otherwise return as a valid `LocInfo.`
            val layeredLoc = spawnedLocs.findLayer(coords, mapLoc1.layer)
            val mapLoc = mapLoc1.takeIf { layeredLoc == null } ?: return null
            return mapLoc.copy(coords = coords)
        }

        // If the width and length are equal, we will obtain the same `findRotatedMapLoc` outcome
        // as above. We return early as we already know the result.
        if (width == length) {
            return null
        }

        val mapLoc2 = findRotatedMapLoc(regionGrid, normalBase, rotation, length, width, loc)
        if (mapLoc2 != null) {
            val layeredLoc = spawnedLocs.findLayer(coords, mapLoc2.layer)
            val mapLoc = mapLoc2.takeIf { layeredLoc == null } ?: return null
            return mapLoc.copy(coords = coords)
        }

        return null
    }

    private fun findRotatedMapLoc(
        regionGrid: ZoneGrid,
        normalBase: CoordGrid,
        rotation: Int,
        width: Int,
        length: Int,
        loc: Int,
    ): LocInfo? {
        val translate = RegionRotations.translateLoc(rotation, regionGrid, width, length)
        val normalCoords = normalBase.translate(translate)
        return mapLocs.findType(normalCoords, loc)
    }

    /**
     * Finds a loc in the given [coords] with the specified [shape].
     *
     * Unlike `NormalLocRegistry.findShape`, this version is **less efficient** due to a lack of
     * direct context. Instead of a targeted lookup, we must iterate over all locs in both
     * [spawnedLocs] and [mapLocs] for the zone to find the first match.
     *
     * While the overall impact should be minimal, excessive calls to this function **should be
     * avoided**. If multiple locs need to be found, consider using [findAll] and filtering the
     * results instead.
     *
     * @return The first matching [LocInfo] with the given shape at the specified [coords], or
     *   `null` if none are found.
     */
    public fun findShape(coords: CoordGrid, shape: Int): LocInfo? {
        val regionZone = ZoneKey.from(coords)
        val zoneLocs = findAll(regionZone)
        return zoneLocs.firstOrNull { it.coords == coords && it.shapeId == shape }
    }

    /**
     * **Important Note:** Locs that originally belonged to [regionZone] but were shifted to another
     * zone due to region rotation are intentionally excluded. This prevents operations on locs that
     * may have moved to unexpected zones.
     */
    public fun findAll(regionZone: ZoneKey): Sequence<LocInfo> {
        val copiedZone = regions[regionZone]
        val normalZone = copiedZone.normalZone()

        // We are trying to be strict about not allowing any operations on "invalid" zones within
        // region areas. (black zones around the region that should be inaccessible)
        // As such, we return an empty sequence early if this is found to be the case.
        if (copiedZone == RegionZoneCopy.NULL) {
            return emptySequence()
        }

        val spawnedLocs = spawnedLocs[regionZone]?.byte2IntEntrySet()
        val staticLocs = mapLocs[normalZone]?.byte2IntEntrySet()

        if (spawnedLocs == null && staticLocs == null) {
            return emptySequence()
        }

        return sequence {
            val processedKeys = ByteOpenHashSet()

            val regionZoneBase = regionZone.toCoords()
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
                    val coords = regionZoneBase.translate(key.x, key.z)
                    val loc = LocInfo(key.layer, coords, entity)
                    yield(loc)
                }
            }

            val normalZoneBase = normalZone.toCoords()
            if (staticLocs != null) {
                for (entry in staticLocs) {
                    val entity = LocEntity(entry.intValue)

                    val locType = locTypes.getValue(entity.id)
                    val locWidth = Rotations.rotate(entity.angle, locType.width, locType.length)
                    val locLength = Rotations.rotate(entity.angle, locType.length, locType.width)

                    val normalKey = LocZoneKey(entry.byteKey)
                    val normalGrid = ZoneGrid(normalKey.x, normalKey.z, normalZone.level)
                    val translate =
                        RegionRotations.translateLoc(
                            regionRot = copiedZone.rotation,
                            locSwGrid = normalGrid,
                            locAdjustedWidth = locWidth,
                            locAdjustedLength = locLength,
                        )
                    val rotatedCoords = normalZoneBase.translate(translate)

                    // Note that the `rotatedCoords` _can_ have a different `ZoneKey` than the
                    // initially supplied `normalKey`. This occurs when the loc rotates with the
                    // zone and its south-west (base) coord is translated into a different zone.
                    // In practice, this should not occur as any map that is created to be used
                    // for regions should take into consideration that/if it's going to be rotated
                    // when used in the game. However, if/when it does occur, we want to ignore
                    // said loc as to avoid an arbitrary operation that may affect an unexpected
                    // zone. (which may even be an "invalid" zone that's not in-use by the region)
                    if (ZoneKey.from(rotatedCoords) != normalZone) {
                        continue
                    }

                    val regionGrid = ZoneGrid.from(rotatedCoords)
                    val regionLocKey = LocZoneKey(regionGrid, normalKey.layer)
                    val regionCoords = regionZoneBase.translate(translate)

                    // Check if the entry's (rotated) byteKey has already been processed by
                    // spawnedLocs. If it has, skip this entry to ensure that any loc from
                    // spawnedLocs takes priority over staticLocs for the same zone grid and layer.
                    if (regionLocKey.packed in processedKeys) {
                        continue
                    }

                    val loc = LocInfo(regionLocKey.layer, regionCoords, entity)
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
     * Deletes a spawned loc entity from the [spawnedLocs] map using the given [regionZone] and
     * updates its respective viewable zone.
     *
     * This function checks if a loc entity exists at the specified [regionZone] and [regionLocKey].
     * If found, it removes the entity, its collision data via [removeLocCollision], _and unlike
     * [removeSpawnedLoc] function, will trigger an update within [updates]_.
     *
     * Additionally, if a static/map loc was previously masked by the "deleted" loc, this function
     * will reapply its collision data as well as trigger a "loc add" update within [updates].
     *
     * @return `true` if the loc entity was removed; `false` otherwise.
     * @see [removeSpawnedLoc]
     */
    private fun deleteSpawnedLoc(
        regionZone: ZoneKey,
        regionLocKey: LocZoneKey,
        normalZone: ZoneKey,
        normalLocKey: LocZoneKey,
        regionRot: Int,
        loc: LocInfo,
    ): Boolean {
        val zone = spawnedLocs[regionZone] ?: return false
        val previousLoc = zone.get(regionLocKey.packed)

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

        val removed = zone.remove(regionLocKey.packed)
        if (removed == zone.defaultReturnValue()) {
            return false
        }

        removeLocCollision(loc)

        val maskedStaticLoc = mapLocs[normalZone]?.getOrDefault(normalLocKey.packed, null)
        if (maskedStaticLoc != null) {
            val originalMaskedLoc = LocEntity(maskedStaticLoc)
            val rotatedAngle = (originalMaskedLoc.angle + regionRot) and ANGLE_BIT_MASK
            val maskedEntity = originalMaskedLoc.copy(angle = rotatedAngle)
            val maskedLoc = LocInfo(loc.layer, loc.coords, maskedEntity)
            addLocCollision(maskedLoc)
            updates.locAdd(maskedLoc)
        } else {
            updates.locDel(loc)
        }

        return true
    }

    /**
     * Marks a static location as deleted based on the given [normalZone] and [normalLocKey].
     *
     * This function does not remove the loc entry from [mapLocs]; instead, it replaces the entry
     * with a special "deleted loc" entity (using [DELETED_LOC_ID]). This ensures:
     * - The deleted loc is ignored in [findAll] and similar functions.
     * - A "loc del" zone update is sent when players enter the affected zone.
     *
     * @return `true` if a static map loc was marked as deleted, `false` otherwise.
     */
    private fun deleteStaticLoc(
        regionZone: ZoneKey,
        regionLocKey: LocZoneKey,
        normalZone: ZoneKey,
        normalLocKey: LocZoneKey,
        loc: LocInfo,
    ): Boolean {
        val staticZone = mapLocs[normalZone] ?: return false
        val staticLoc = staticZone.getOrDefault(normalLocKey.packed, null) ?: return false

        // This makes sure that the [loc] given as input matches the same id, shape, and angle as
        // the loc found in the given zone grid with the same loc layer.
        // This acts as a safeguard against trying to delete a loc info that may share the same
        // layer and zone grid as the found loc, but does not share other metadata such as its
        // type, which could lead to incorrect data being used for i.e., removing its collision.
        if (loc.entity.packed != staticLoc) {
            return false
        }

        val deletedLoc = LocEntity(staticLoc).copy(id = DELETED_LOC_ID, angle = loc.angleId)
        spawnedLocs[regionZone, regionLocKey] = deletedLoc
        removeLocCollision(loc)
        updates.locDel(loc)

        return true
    }

    private fun mapLocExists(loc: LocInfo, region: Region, copiedZone: RegionZoneCopy): Boolean {
        val remappedCoords = region.remappedLocCoords(loc.coords)
        if (remappedCoords != null) {
            val remappedZone = ZoneKey.from(remappedCoords)
            val remappedGrid = ZoneGrid.from(remappedCoords)
            val remappedLocKey = LocZoneKey(remappedGrid, loc.layer)

            val remappedMapLoc = mapLocs[remappedZone]?.getOrDefault(remappedLocKey.packed, null)
            val remappedEntity = remappedMapLoc?.let(::LocEntity)

            if (remappedEntity?.id == loc.id && remappedEntity.shape == loc.shapeId) {
                return true
            }
        }

        val (normalZone, normalLocKey) = resolveNormalMapping(loc, copiedZone)
        val normalMapLoc = mapLocs[normalZone]?.getOrDefault(normalLocKey.packed, null)
        val normalEntity = normalMapLoc?.let(::LocEntity) ?: return false

        return normalEntity.id == loc.id && normalEntity.shape == loc.shapeId
    }

    private fun resolveNormalMapping(loc: LocInfo, copiedZone: RegionZoneCopy): NormalMapping {
        val regionZoneGrid = ZoneGrid.from(loc.coords)
        val knownNormalZone = copiedZone.normalZone()
        val normalBase = knownNormalZone.toCoords()
        val inverseRotation = copiedZone.inverseRotation

        val locType = locTypes[loc]
        val normalLocAngle = (loc.angleId - copiedZone.rotation) and ANGLE_BIT_MASK
        val normalLocWidth = Rotations.rotate(normalLocAngle, locType.width, locType.length)
        val normalLocLength = Rotations.rotate(normalLocAngle, locType.length, locType.width)
        val normalTranslation =
            RegionRotations.translateLoc(
                regionRot = inverseRotation,
                locSwGrid = regionZoneGrid,
                locAdjustedWidth = normalLocWidth,
                locAdjustedLength = normalLocLength,
            )

        val normalCoord = normalBase.translate(normalTranslation)
        val normalGrid = ZoneGrid.from(normalCoord)
        val normalLocKey = LocZoneKey(normalGrid, loc.layer)
        val translatedNormalZone = ZoneKey.from(normalCoord)

        return NormalMapping(translatedNormalZone, normalLocKey)
    }

    private data class NormalMapping(val normalZone: ZoneKey, val normalLocKey: LocZoneKey)

    private fun addLocCollision(loc: LocInfo) {
        val type = locTypes[loc.id] ?: return
        collision.addLoc(loc, type)
    }

    private fun removeLocCollision(loc: LocInfo) {
        val type = locTypes[loc.id] ?: return
        collision.removeLoc(loc, type)
    }

    public companion object {
        public val DELETED_LOC_ID: Int = LocRegistry.DELETED_LOC_ID
        private const val ANGLE_BIT_MASK: Int = LocEntity.ANGLE_BIT_MASK
    }
}
