package org.runerealm.api.core.module

import net.rsprot.protocol.api.util.ZonePartialEnclosedCacheBuffer
import org.runerealm.api.registry.zone.ZonePlayerActivityBitSet
import org.runerealm.api.registry.zone.ZoneUpdateMap
import org.runerealm.game.area.AreaIndex
import org.runerealm.game.map.LocZoneStorage
import org.runerealm.module.ExtendedModule
import org.runerealm.routefinder.collision.CollisionFlagMap

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
