package org.runerealm.api.testing.factory.region

import org.runerealm.api.registry.controller.ControllerRegistry
import org.runerealm.api.registry.loc.LocRegistryNormal
import org.runerealm.api.registry.npc.NpcRegistry
import org.runerealm.api.registry.region.RegionRegistry
import org.runerealm.api.registry.zone.ZonePlayerActivityBitSet
import org.runerealm.events.EventBus
import org.runerealm.game.MapClock
import org.runerealm.game.entity.ControllerList
import org.runerealm.game.entity.NpcList
import org.runerealm.game.map.LocZoneStorage
import org.runerealm.game.region.RegionListLarge
import org.runerealm.game.region.RegionListSmall
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.routefinder.collision.CollisionFlagMap

public class TestRegionRegistryFactory {
    public fun create(
        collision: CollisionFlagMap,
        locReg: LocRegistryNormal,
        locTypes: LocTypeList,
        locZones: LocZoneStorage,
        smallRegions: RegionListSmall = RegionListSmall(),
        largeRegions: RegionListLarge = RegionListLarge(),
        zoneActivity: ZonePlayerActivityBitSet = ZonePlayerActivityBitSet(),
        npcRegistry: NpcRegistry = NpcRegistry(NpcList(), collision, EventBus()),
        controllerReg: ControllerRegistry = ControllerRegistry(MapClock(), ControllerList()),
    ): RegionRegistry =
        RegionRegistry(
            smallRegions,
            largeRegions,
            locReg,
            collision,
            locTypes,
            locZones,
            npcRegistry,
            controllerReg,
            zoneActivity,
        )
}
