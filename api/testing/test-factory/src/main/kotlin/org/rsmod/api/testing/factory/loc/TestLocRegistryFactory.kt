package org.rsmod.api.testing.factory.loc

import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.api.testing.factory.map.TestCollisionFactory
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.pathfinder.collision.CollisionFlagMap

public class TestLocRegistryFactory {
    public fun create(
        collision: CollisionFlagMap = createDefaultCollision(),
        zones: ZoneUpdateMap = ZoneUpdateMap(),
        types: LocTypeList = createDefaultTypes(),
    ): LocRegistry = LocRegistry(zones, collision, types)

    private fun createDefaultCollision(): CollisionFlagMap = TestCollisionFactory().create()

    private fun createDefaultTypes(): LocTypeList = TestLocTypeListFactory().createDefault()
}
