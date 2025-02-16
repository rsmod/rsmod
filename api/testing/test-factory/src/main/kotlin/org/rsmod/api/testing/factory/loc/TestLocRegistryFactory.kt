package org.rsmod.api.testing.factory.loc

import org.rsmod.api.registry.loc.LocRegistryNormal
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.api.testing.factory.map.TestCollisionFactory
import org.rsmod.game.map.LocZoneStorage
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.routefinder.collision.CollisionFlagMap

public class TestLocRegistryFactory {
    public fun createNormal(
        collision: CollisionFlagMap = createDefaultCollision(),
        locZones: LocZoneStorage = LocZoneStorage(),
        zoneUpdates: ZoneUpdateMap = ZoneUpdateMap(),
        locTypes: LocTypeList = createDefaultTypes(),
    ): LocRegistryNormal = LocRegistryNormal(zoneUpdates, collision, locTypes, locZones)

    private fun createDefaultCollision(): CollisionFlagMap = TestCollisionFactory().create()

    private fun createDefaultTypes(): LocTypeList = TestLocTypeListFactory().createDefault()
}
