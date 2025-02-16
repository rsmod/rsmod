package org.rsmod.api.testing.factory.region

import org.rsmod.api.registry.loc.LocRegistryNormal
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.api.registry.zone.ZonePlayerActivityBitSet
import org.rsmod.game.region.RegionListLarge
import org.rsmod.game.region.RegionListSmall
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.routefinder.collision.CollisionFlagMap

public class TestRegionRegistryFactory {
    public fun create(
        collision: CollisionFlagMap,
        locReg: LocRegistryNormal,
        locTypes: LocTypeList,
        smallRegions: RegionListSmall = RegionListSmall(),
        largeRegions: RegionListLarge = RegionListLarge(),
        zoneActivity: ZonePlayerActivityBitSet = ZonePlayerActivityBitSet(),
    ): RegionRegistry =
        RegionRegistry(smallRegions, largeRegions, locReg, collision, locTypes, zoneActivity)
}
