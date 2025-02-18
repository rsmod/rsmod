package org.rsmod.api.testing.factory.region

import org.rsmod.api.registry.controller.ControllerRegistry
import org.rsmod.api.registry.loc.LocRegistryNormal
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.api.registry.zone.ZonePlayerActivityBitSet
import org.rsmod.events.EventBus
import org.rsmod.game.MapClock
import org.rsmod.game.entity.ControllerList
import org.rsmod.game.entity.NpcList
import org.rsmod.game.map.LocZoneStorage
import org.rsmod.game.region.RegionListLarge
import org.rsmod.game.region.RegionListSmall
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.routefinder.collision.CollisionFlagMap

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
