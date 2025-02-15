package org.rsmod.api.testing.factory.loc

import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.api.testing.factory.map.TestCollisionFactory
import org.rsmod.game.map.LocZoneStorage
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.routefinder.collision.CollisionFlagMap

public class TestLocRegistryFactory {
    public fun create(
        collision: CollisionFlagMap = createDefaultCollision(),
        locZones: LocZoneStorage = LocZoneStorage(),
        zoneUpdates: ZoneUpdateMap = ZoneUpdateMap(),
        locTypes: LocTypeList = createDefaultTypes(),
    ): LocRegistry = LocRegistry(zoneUpdates, collision, locTypes, locZones)

    private fun createDefaultCollision(): CollisionFlagMap = TestCollisionFactory().create()

    private fun createDefaultTypes(): LocTypeList = TestLocTypeListFactory().createDefault()
}
