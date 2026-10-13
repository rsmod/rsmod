package org.runerealm.api.testing.factory.loc

import org.runerealm.api.registry.loc.LocRegistryNormal
import org.runerealm.api.registry.loc.LocRegistryRegion
import org.runerealm.api.registry.region.RegionRegistry
import org.runerealm.api.registry.zone.ZoneUpdateMap
import org.runerealm.api.testing.factory.map.TestCollisionFactory
import org.runerealm.api.testing.factory.regionRegistryFactory
import org.runerealm.game.map.LocZoneStorage
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.routefinder.collision.CollisionFlagMap

public class TestLocRegistryFactory {
    public fun createNormal(
        collision: CollisionFlagMap = createDefaultCollision(),
        locZones: LocZoneStorage = LocZoneStorage(),
        zoneUpdates: ZoneUpdateMap = ZoneUpdateMap(),
        locTypes: LocTypeList = createDefaultTypes(),
    ): LocRegistryNormal = LocRegistryNormal(zoneUpdates, collision, locTypes, locZones)

    public fun createRegion(
        normalLocReg: LocRegistryNormal,
        collision: CollisionFlagMap = createDefaultCollision(),
        locZones: LocZoneStorage = LocZoneStorage(),
        zoneUpdates: ZoneUpdateMap = ZoneUpdateMap(),
        locTypes: LocTypeList = createDefaultTypes(),
        regionReg: RegionRegistry =
            regionRegistryFactory.create(collision, normalLocReg, locTypes, locZones),
    ): LocRegistryRegion = LocRegistryRegion(zoneUpdates, collision, locTypes, locZones, regionReg)

    private fun createDefaultCollision(): CollisionFlagMap = TestCollisionFactory().create()

    private fun createDefaultTypes(): LocTypeList = TestLocTypeListFactory().createDefault()
}
