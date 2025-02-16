package org.rsmod.api.repo.region

import jakarta.inject.Inject
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.api.registry.region.isSuccess
import org.rsmod.game.region.Region
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey

public class RegionRepository @Inject constructor(private val registry: RegionRegistry) {
    /**
     * Attempts to allocate a region using the provided [template].
     *
     * The function determines whether the region should be **small** or **large** based on the
     * template configuration. If the template represents a **large** region, it calls [addLarge].
     * Otherwise, it attempts to allocate a small region via [addSmall].
     *
     * If no space is available for a **small** region, the function will automatically attempt to
     * allocate a **large** region instead.
     *
     * @return The allocated [Region], or `null` if allocation fails due to capacity limits.
     * @see [addSmall]
     * @see [addLarge]
     */
    public fun add(template: RegionTemplate): Region? =
        if (template.isLargeRegion()) {
            addLarge(template)
        } else {
            addSmall(template)
        }

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
    private fun addSmall(template: RegionTemplate): Region? {
        val result = registerSmall(template)
        if (result !is Result.Add.Success) {
            return addLarge(template)
        }
        return result.region
    }

    private fun addLarge(template: RegionTemplate): Region? {
        val result = registerLarge(template)
        if (result !is Result.Add.Success) {
            return null
        }
        return result.region
    }

    public fun registerSmall(template: RegionTemplate): Result.Add {
        registry.removeInactiveSmallRegions()

        val result = registry.registerSmall()
        if (!result.isSuccess()) {
            return Result.Add.RegionCapacityReached
        }

        val region = result.region
        val southWestZone = ZoneKey.from(region.southWest)
        val copyZones = template.build(southWestZone)

        registry.build(region, copyZones)

        return Result.Add.Registered(region)
    }

    public fun registerLarge(template: RegionTemplate): Result.Add {
        registry.removeInactiveLargeRegions()

        val result = registry.registerLarge()
        if (!result.isSuccess()) {
            return Result.Add.RegionCapacityReached
        }

        val region = result.region
        val southWestZone = ZoneKey.from(region.southWest)
        val copyZones = template.build(southWestZone)

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

    public companion object {
        private const val SMALL_REGION_SQ_LENGTH = RegionRegistry.SMALL_REGION_SQUARE_LENGTH
        public const val SMALL_REGION_ZONE_LENGTH: Int = SMALL_REGION_SQ_LENGTH / ZoneGrid.LENGTH

        private const val LARGE_REGION_SQ_LENGTH = RegionRegistry.LARGE_REGION_SQUARE_LENGTH
        public const val LARGE_REGION_ZONE_LENGTH: Int = LARGE_REGION_SQ_LENGTH / ZoneGrid.LENGTH
    }
}
