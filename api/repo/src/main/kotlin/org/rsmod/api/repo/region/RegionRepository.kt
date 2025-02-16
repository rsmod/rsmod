package org.rsmod.api.repo.region

import jakarta.inject.Inject
import kotlin.apply
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.api.registry.region.isSuccess
import org.rsmod.game.region.Region
import org.rsmod.game.region.zone.RegionZoneBuilder
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey

public class RegionRepository @Inject constructor(private val registry: RegionRegistry) {
    /**
     * Attempts to allocate a **small** region. If there is no available space in the small-region
     * working area, this function will attempt to allocate a **large** region instead.
     *
     * If there is no available space in either the small-region or large-region working areas, this
     * function will return `null`.
     *
     * This behavior follows the original intended mechanic, where a small region that cannot be
     * allocated due to restricted space falls back to a large region.
     */
    public fun add(zones: RegionZoneBuilder.() -> Unit): Region? {
        val result = registerSmall(zones)
        if (result !is Result.Add.Success) {
            return addLarge(zones)
        }
        return result.region
    }

    public fun addLarge(zones: RegionZoneBuilder.() -> Unit): Region? {
        val result = registerLarge(zones)
        if (result !is Result.Add.Success) {
            return null
        }
        return result.region
    }

    public fun registerSmall(zones: RegionZoneBuilder.() -> Unit): Result.Add {
        registry.removeInactiveSmallRegions()

        val result = registry.registerSmall()
        if (!result.isSuccess()) {
            return Result.Add.RegionCapacityReached
        }
        val region = result.region
        val southWestZone = ZoneKey.from(region.southWest)

        val zoneBuilder = RegionZoneBuilder(SMALL_REGION_ZONE_LENGTH).apply(zones)
        val copyZones = zoneBuilder.build(southWestZone)
        registry.build(region, copyZones)

        return Result.Add.Registered(region)
    }

    public fun registerLarge(zones: RegionZoneBuilder.() -> Unit): Result.Add {
        registry.removeInactiveLargeRegions()

        val result = registry.registerLarge()
        if (!result.isSuccess()) {
            return Result.Add.RegionCapacityReached
        }
        val region = result.region
        val southWestZone = ZoneKey.from(region.southWest)

        val zoneBuilder = RegionZoneBuilder(LARGE_REGION_ZONE_LENGTH).apply(zones)
        val copyZones = zoneBuilder.build(southWestZone)
        registry.build(region, copyZones)

        return Result.Add.Registered(region)
    }

    public class Result {
        public sealed class Add {
            public sealed class Success(public val region: Region) : Add()

            public sealed class Failure : Add()

            public class Registered(region: Region) : Success(region)

            public data object RegionCapacityReached : Failure()
        }
    }

    private companion object {
        private const val SMALL_REGION_SQUARE_LENGTH = RegionRegistry.SMALL_REGION_SQUARE_LENGTH
        private const val SMALL_REGION_ZONE_LENGTH = SMALL_REGION_SQUARE_LENGTH / ZoneGrid.LENGTH

        private const val LARGE_REGION_SQUARE_LENGTH = RegionRegistry.LARGE_REGION_SQUARE_LENGTH
        private const val LARGE_REGION_ZONE_LENGTH = LARGE_REGION_SQUARE_LENGTH / ZoneGrid.LENGTH
    }
}
