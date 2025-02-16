package org.rsmod.api.core.module

import net.rsprot.protocol.api.util.ZonePartialEnclosedCacheBuffer
import org.rsmod.api.registry.controller.ControllerRegistry
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.loc.LocRegistryNormal
import org.rsmod.api.registry.loc.LocRegistryRegion
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.game.map.LocZoneStorage
import org.rsmod.module.ExtendedModule
import org.rsmod.routefinder.collision.CollisionFlagMap

public object GameMapModule : ExtendedModule() {
    override fun bind() {
        bindInstance<CollisionFlagMap>()
        bindInstance<LocZoneStorage>()
        bindInstance<LocRegistry>()
        bindInstance<LocRegistryNormal>()
        bindInstance<LocRegistryRegion>()
        bindInstance<NpcRegistry>()
        bindInstance<ObjRegistry>()
        bindInstance<PlayerRegistry>()
        bindInstance<ControllerRegistry>()
        bindInstance<RegionRegistry>()
        bindInstance<ZonePartialEnclosedCacheBuffer>()
        bindInstance<ZoneUpdateMap>()
    }
}
