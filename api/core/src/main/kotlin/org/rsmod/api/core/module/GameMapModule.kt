package org.rsmod.api.core.module

import net.rsprot.protocol.api.util.ZonePartialEnclosedCacheBuffer
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.module.ExtendedModule
import org.rsmod.routefinder.collision.CollisionFlagMap

public object GameMapModule : ExtendedModule() {
    override fun bind() {
        bindInstance<CollisionFlagMap>()
        bindInstance<LocRegistry>()
        bindInstance<NpcRegistry>()
        bindInstance<ObjRegistry>()
        bindInstance<ZonePartialEnclosedCacheBuffer>()
        bindInstance<ZoneUpdateMap>()
    }
}
