package org.rsmod.content.areas.city.lumbridge.spawns

import org.rsmod.api.type.builders.map.obj.MapObjSpawnBuilder
import org.rsmod.content.areas.city.lumbridge.LumbridgeScript

object LumbridgeObjSpawns : MapObjSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<LumbridgeScript>("objs.toml")
    }
}
