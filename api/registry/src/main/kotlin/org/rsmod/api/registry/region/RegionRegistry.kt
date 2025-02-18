package org.rsmod.api.registry.region

import jakarta.inject.Inject
import org.rsmod.api.registry.loc.LocRegistryNormal
import org.rsmod.api.registry.zone.ZonePlayerActivityBitSet
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.map.collision.add
import org.rsmod.game.map.collision.addLoc
import org.rsmod.game.region.Region
import org.rsmod.game.region.Region.Companion.INVALID_SLOT
import org.rsmod.game.region.RegionListLarge
import org.rsmod.game.region.RegionListSmall
import org.rsmod.game.region.util.RegionRotations
import org.rsmod.game.region.zone.RegionZoneCopy
import org.rsmod.game.region.zone.RegionZoneCopyMap
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.flag.CollisionFlag
import org.rsmod.routefinder.util.Rotations

public class RegionRegistry
@Inject
constructor(
    private val smallRegions: RegionListSmall,
    private val largeRegions: RegionListLarge,
    private val normalLocReg: LocRegistryNormal,
    private val collision: CollisionFlagMap,
    private val locTypes: LocTypeList,
    private val zoneActivity: ZonePlayerActivityBitSet,
) {
    init {
        workingAreaSmall.assertValidBounds(smallRegions.capacity)
        workingAreaLarge.assertValidBounds(largeRegions.capacity)
    }

    private val zones: RegionZoneCopyMap = RegionZoneCopyMap()
    private var uid = 0

    public fun registerSmall(): RegionRegistryResult.Add {
        val slot = smallRegions.nextFreeSlot() ?: return RegionRegistryResult.Add.NoAvailableSlot

        val squareLength = SMALL_REGION_SQUARE_LENGTH
        val southWest = workingAreaSmall.calculateCoord(slot)
        val northEast = southWest.translate(squareLength, squareLength)

        val uid = uid++

        val region = Region(southWest, northEast, uid, slot)
        smallRegions[slot] = region
        return RegionRegistryResult.Add.CreateSmall(region)
    }

    public fun registerLarge(): RegionRegistryResult.Add {
        val slot = largeRegions.nextFreeSlot() ?: return RegionRegistryResult.Add.NoAvailableSlot

        val squareLength = LARGE_REGION_SQUARE_LENGTH
        val southWest = workingAreaLarge.calculateCoord(slot)
        val northEast = southWest.translate(squareLength, squareLength)

        val uid = uid++

        val region = Region(southWest, northEast, uid, slot)
        largeRegions[slot] = region
        return RegionRegistryResult.Add.CreateLarge(region)
    }

    public fun unregister(region: Region): RegionRegistryResult.Delete =
        when (region.southWest) {
            in workingAreaSmall -> unregisterSmall(region)
            in workingAreaLarge -> unregisterLarge(region)
            else -> error("Coords not associated with a region area: region=$region")
        }

    private fun unregisterSmall(region: Region): RegionRegistryResult.Delete {
        if (region.slot == INVALID_SLOT) {
            return RegionRegistryResult.Delete.UnexpectedSlot
        }

        if (smallRegions[region.slot] != region) {
            return RegionRegistryResult.Delete.ListSlotMismatch(smallRegions[region.slot])
        }

        clearAllZones(region)
        smallRegions.remove(region.slot)
        region.slot = INVALID_SLOT

        return RegionRegistryResult.Delete.RemoveSmall
    }

    private fun unregisterLarge(region: Region): RegionRegistryResult.Delete {
        if (region.slot == INVALID_SLOT) {
            return RegionRegistryResult.Delete.UnexpectedSlot
        }

        if (largeRegions[region.slot] != region) {
            return RegionRegistryResult.Delete.ListSlotMismatch(largeRegions[region.slot])
        }

        clearAllZones(region)
        largeRegions.remove(region.slot)
        region.slot = INVALID_SLOT

        return RegionRegistryResult.Delete.RemoveLarge
    }

    public fun removeInactiveSmallRegions() {
        val filtered = smallRegions.filter(::isEmpty)
        for (region in filtered) {
            unregisterSmall(region)
        }
    }

    public fun removeInactiveLargeRegions() {
        val filtered = largeRegions.filter(::isEmpty)
        for (region in filtered) {
            unregisterLarge(region)
        }
    }

    public fun isEmpty(region: Region): Boolean {
        return !zoneActivity.isAnyLevelFlagged(region.southWestZone, region.northEastZone)
    }

    public fun isValid(regionSlot: Int, regionUid: Int): Boolean {
        val validSmallRegion = smallRegions[regionSlot]?.uid == regionUid
        if (validSmallRegion) {
            return true
        }
        val validLargeRegion = largeRegions[regionSlot]?.uid == regionUid
        return validLargeRegion
    }

    public operator fun get(coords: CoordGrid): Region? {
        if (coords in workingAreaSmall) {
            val slot = workingAreaSmall.calculateSlot(coords)
            return smallRegions[slot]
        }

        if (coords in workingAreaLarge) {
            val slot = workingAreaLarge.calculateSlot(coords)
            return largeRegions[slot]
        }

        return null
    }

    public operator fun get(zone: ZoneKey): RegionZoneCopy = zones[zone]

    /**
     * Constructs a [region] by registering and initializing its zones based on [copyZones].
     *
     * This function performs the following steps:
     * - Registers all valid zones within the region using [registerZones].
     * - Clears any collision flags from **unoccupied zones** within the region using
     *   [clearNullRegionZones].
     * - Removes any **bordering zones** that were allocated but are not part of the region’s usable
     *   area using [clearNullBorderZones].
     */
    public fun build(region: Region, copyZones: RegionZoneCopyMap) {
        registerZones(region, copyZones)
        clearNullRegionZones(region)
        clearNullBorderZones(region)
    }

    private fun registerZones(region: Region, copyZones: RegionZoneCopyMap) {
        val regionZones = region.toZoneList()
        for (regionZone in regionZones) {
            val copyZone = copyZones[regionZone]
            if (copyZone == RegionZoneCopy.NULL) {
                continue
            }
            registerZone(region, regionZone, copyZone)
        }
    }

    public fun registerZone(region: Region, regionZone: ZoneKey, copyZone: RegionZoneCopy) {
        require(copyZone != RegionZoneCopy.NULL) { "`copyZone` cannot be null." }
        check(regionZone !in zones) { "Region zone is already occupied: ${zones[regionZone]}" }

        val regionBase = regionZone.toCoords()
        val normalZone = copyZone.normalZone()
        val normalBase = normalZone.toCoords()

        val rotation = copyZone.rotation

        // Some collision flags are embedded in the map files within the cache, rather than being
        // applied by spawned locs from the loc files.
        //
        // These map-file-based collision flags must be accounted for separately when building the
        // region. To do this, we iterate through the zone's coordinates, extract only the relevant
        // collision flags, and apply them to the corresponding translated region coordinates.
        for (x in 0 until ZoneGrid.LENGTH) {
            for (z in 0 until ZoneGrid.LENGTH) {
                val coordX = normalBase.x + x
                val coordZ = normalBase.z + z
                val coordLevel = normalBase.level

                val fullFlags = collision[coordX, coordZ, coordLevel]
                val extractedFlags = fullFlags and (CollisionFlag.ROOF or CollisionFlag.BLOCK_WALK)

                if (extractedFlags == 0) {
                    continue
                }

                val normalGrid = ZoneGrid(x, z, coordLevel)
                val translation = RegionRotations.translateCoords(rotation, normalGrid)
                val regionCoords = regionBase.translate(translation)

                collision.add(regionCoords, extractedFlags)
            }
        }

        // Copy and apply the respective collision flags based on the currently found locs within
        // the normal zone.
        val normalLocs = normalLocReg.findAll(normalZone)
        for (normalLoc in normalLocs) {
            val locType = locTypes[normalLoc]

            val width = Rotations.rotate(normalLoc.angleId, locType.width, locType.length)
            val length = Rotations.rotate(normalLoc.angleId, locType.length, locType.width)

            val normalGrid = ZoneGrid.from(normalLoc.coords)
            val translation = RegionRotations.translateLoc(rotation, normalGrid, width, length)

            val regionAngle = (normalLoc.angleId + rotation) and LocEntity.ANGLE_BIT_MASK
            val regionEntity = normalLoc.entity.copy(angle = regionAngle)
            val regionCoords = regionBase.translate(translation)
            val regionLoc = normalLoc.copy(coords = regionCoords, entity = regionEntity)

            collision.addLoc(regionLoc, locType)

            // Some locs may be translated **out** of the expected region zone given the right
            // combination of their dimensions, angle, and region rotation. Normally, our region
            // implementation looks up the "untranslated" normal loc based on the region zone it is
            // currently in. However, in these cases, the translated loc now belongs to a different
            // zone, which breaks this lookup.
            //
            // To fix this, we detect when a loc moves **out of its expected zone bounds** (e.g.,
            // its translation moves it outside the region zone). When this happens, we store the
            // loc's **original normal coordinates** alongside its **new region coordinates** so
            // that future lookups still resolve correctly.
            val shiftedZones = translation.x < 0 || translation.z < 0
            if (shiftedZones) {
                // We store these "remapped" loc coordinates within `region` rather than in this
                // registry class to simplify cleanup when a region is deleted. If we stored them
                // globally in this registry, we would need an additional mechanism to track which
                // remapped locs belong to which region - either via an explicit mapping or by
                // iterating through all of them on region deletion.
                region.remapLocCoords(regionLoc.coords, normalLoc.coords)
            }
        }

        zones[regionZone] = copyZone

        // Trigger the region's zone registration.
        region.registerZone(copyZone, regionZone)
    }

    /**
     * Removes any collision flags from empty zones in [region].
     *
     * This includes any zones that have not been registered through [registerZone] or have a value
     * of [RegionZoneCopy.NULL] in the [zones] map.
     *
     * This function runs for all four levels (0-3) to ensure consistency across all levels.
     *
     * @see [CollisionFlagMap.deallocateIfPresent]
     */
    public fun clearNullRegionZones(region: Region) {
        clearNullRegionZones(region, level = 0)
        clearNullRegionZones(region, level = 1)
        clearNullRegionZones(region, level = 2)
        clearNullRegionZones(region, level = 3)
    }

    /**
     * Removes any collision flags from empty zones in [region] at the specified [level].
     *
     * This includes any zones that have not been registered through [registerZone] or have a value
     * of [RegionZoneCopy.NULL] in the [zones] map.
     *
     * Unlike [clearNullRegionZones], which operates on all four levels (0-3), this function only
     * clears zones at the given [level].
     *
     * @see [CollisionFlagMap.deallocateIfPresent]
     */
    public fun clearNullRegionZones(region: Region, level: Int) {
        val southWest = region.southWest
        val zoneLength = (region.northEast.x - southWest.x) / ZoneGrid.LENGTH
        for (x in 0 until zoneLength) {
            for (z in 0 until zoneLength) {
                val coordX = southWest.x + (x * ZoneGrid.LENGTH)
                val coordZ = southWest.z + (z * ZoneGrid.LENGTH)

                val zone = ZoneKey.fromAbsolute(coordX, coordZ, level)
                if (zone in zones) {
                    continue
                }

                collision.deallocateIfPresent(coordX, coordZ, level)
            }
        }
    }

    /**
     * Removes any collision flags from **bordering zones** around [region] that were allocated but
     * are not part of the region's usable area.
     *
     * These "buffer zones" exist adjacent to the region and may have been allocated due to region
     * rotation transformations. This function ensures they do not retain unnecessary collision
     * flags.
     *
     * This function runs for all four levels (0-3) to ensure consistency across all levels.
     *
     * @see [CollisionFlagMap.deallocateIfPresent]
     */
    public fun clearNullBorderZones(region: Region) {
        clearNullBorderZones(region, level = 0)
        clearNullBorderZones(region, level = 1)
        clearNullBorderZones(region, level = 2)
        clearNullBorderZones(region, level = 3)
    }

    /**
     * Removes any collision flags from **bordering zones** around [region] at the specified [level]
     * that were allocated but are not part of the region's usable area.
     *
     * These "buffer zones" exist adjacent to the region and may have been allocated due to region
     * rotation transformations. This function ensures they do not retain unnecessary collision
     * flags.
     *
     * Unlike [clearNullBorderZones], which operates on all four levels (0-3), this function only
     * clears zones at the given [level].
     *
     * @see [CollisionFlagMap.deallocateIfPresent]
     */
    public fun clearNullBorderZones(region: Region, level: Int) {
        val southWest = region.southWest
        val zoneLength = (region.northEast.x - southWest.x) / ZoneGrid.LENGTH

        for (x in 0 until zoneLength) {
            val coordX = southWest.x + (x * ZoneGrid.LENGTH)
            val coordZ = southWest.z - ZoneGrid.LENGTH
            collision.deallocateIfPresent(coordX, coordZ, level)
        }

        for (x in 0 until zoneLength) {
            val coordX = southWest.x + (x * ZoneGrid.LENGTH)
            val coordZ = southWest.z + (zoneLength * ZoneGrid.LENGTH)
            collision.deallocateIfPresent(coordX, coordZ, level)
        }

        for (z in 0 until zoneLength) {
            val coordX = southWest.x - ZoneGrid.LENGTH
            val coordZ = southWest.z + (z * ZoneGrid.LENGTH)
            collision.deallocateIfPresent(coordX, coordZ, level)
        }

        for (z in 0 until zoneLength) {
            val coordX = southWest.x + (zoneLength * ZoneGrid.LENGTH)
            val coordZ = southWest.z + (z * ZoneGrid.LENGTH)
            collision.deallocateIfPresent(coordX, coordZ, level)
        }
    }

    private fun clearAllZones(region: Region) {
        // We _are_ iterating two times through the zones list. This is in hopes that the
        // `collision` operations can benefit from frequent access instead of having to jump
        // between `zones` map and the internal int array from `collision`.
        // All in all, if this does require a performance boost (**extremely unlikely**) we should
        // base our decision off of benchmarks.
        val regionZones = region.toZoneList().filter(zones::remove)
        for (zone in regionZones) {
            val coords = zone.toCoords()
            collision.deallocateIfPresent(coords.x, coords.z, coords.level)
        }
    }

    public data class WorkingArea(
        val horizontalRegionCap: Int,
        val verticalRegionCap: Int,
        val regionSquareLength: Int,
        val startCoordX: Int,
        val startCoordZ: Int,
        val maxCoordZ: Int,
        val maxCoordX: Int,
    ) {
        private val workingRegionLength: Int
            get() = PADDING_SQUARES + regionSquareLength + PADDING_SQUARES

        public operator fun contains(coords: CoordGrid): Boolean =
            coords.x in startCoordX..maxCoordX && coords.z in startCoordZ..maxCoordZ

        public fun calculateSlot(coords: CoordGrid): Int {
            require(coords in this) {
                "`coords` is not within the working area: $coords (workingArea=$this)"
            }
            val relativeX = coords.x - startCoordX
            val relativeZ = coords.z - startCoordZ
            val regionX = relativeX / workingRegionLength
            val regionZ = relativeZ / workingRegionLength
            return (regionZ * horizontalRegionCap) + regionX
        }

        public fun calculateCoord(slot: Int): CoordGrid {
            val regionX = slot % horizontalRegionCap
            val regionZ = slot / horizontalRegionCap
            val coordX = startCoordX + (regionX * workingRegionLength) + PADDING_SQUARES
            val coordZ = startCoordZ + (regionZ * workingRegionLength) + PADDING_SQUARES
            val coord = CoordGrid(coordX, coordZ)
            check(coord in this) { "Unexpected coord result: slot=$slot, coord=$coord" }
            return coord
        }

        public fun assertValidBounds(regionCapacity: Int) {
            val endX = startCoordX + (horizontalRegionCap * workingRegionLength)
            check(endX <= maxCoordX) {
                "Working area cannot hold $horizontalRegionCap horizontal " +
                    "regions as it goes out of expected bounds. " +
                    "(maxCoordX=$maxCoordX, workingAreaEndX=$endX)"
            }

            val endZ = startCoordZ + (verticalRegionCap * workingRegionLength)
            check(endZ <= maxCoordZ) {
                "Working area cannot hold $verticalRegionCap vertical " +
                    "regions as it goes out of expected bounds. " +
                    "(maxCoordZ=$maxCoordZ, workingAreaEndZ=$endZ)"
            }

            val totalRegions = horizontalRegionCap * verticalRegionCap
            check(totalRegions <= regionCapacity) {
                "Working area cannot hold $totalRegions as it goes " +
                    "beyond expected list capacity. (capacity=$regionCapacity)"
            }
        }
    }

    public companion object {
        public const val PADDING_SQUARES: Int = 32

        public const val START_COORD_X: Int = 6400 + PADDING_SQUARES
        public const val START_COORD_Z: Int = 0 + PADDING_SQUARES

        public const val SMALL_REGION_SQUARE_LENGTH: Int = 128
        public const val LARGE_REGION_SQUARE_LENGTH: Int = 320

        public val workingAreaSmall: WorkingArea =
            WorkingArea(
                horizontalRegionCap = 19,
                verticalRegionCap = 85,
                regionSquareLength = SMALL_REGION_SQUARE_LENGTH,
                startCoordX = START_COORD_X + 3904,
                startCoordZ = START_COORD_Z,
                maxCoordX = CoordGrid.MAP_WIDTH,
                maxCoordZ = CoordGrid.MAP_LENGTH,
            )

        public val workingAreaLarge: WorkingArea =
            WorkingArea(
                horizontalRegionCap = 10,
                verticalRegionCap = 42,
                regionSquareLength = LARGE_REGION_SQUARE_LENGTH,
                startCoordX = START_COORD_X,
                startCoordZ = START_COORD_Z,
                maxCoordX = CoordGrid.MAP_WIDTH,
                maxCoordZ = CoordGrid.MAP_LENGTH,
            )

        public fun inWorkingArea(coords: CoordGrid): Boolean = coords.x >= START_COORD_X

        public fun inWorkingArea(key: ZoneKey): Boolean = key.x * ZoneGrid.LENGTH >= START_COORD_X
    }
}
