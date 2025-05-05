package org.rsmod.api.core.module

import net.rsprot.protocol.api.util.ZonePartialEnclosedCacheBuffer
import org.rsmod.api.registry.zone.ZonePlayerActivityBitSet
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.game.area.AreaIndex
import org.rsmod.game.map.LocZoneStorage
import org.rsmod.module.ExtendedModule
import org.rsmod.routefinder.collision.CollisionFlagMap

public object GameMapModule : ExtendedModule() {
    override fun bind() {
        bindInstance<AreaIndex>()
        bindInstance<CollisionFlagMap>()
        bindInstance<LocZoneStorage>()
        bindInstance<ZonePlayerActivityBitSet>()
        bindInstance<ZonePartialEnclosedCacheBuffer>()
        bindInstance<ZoneUpdateMap>()
    }
}
