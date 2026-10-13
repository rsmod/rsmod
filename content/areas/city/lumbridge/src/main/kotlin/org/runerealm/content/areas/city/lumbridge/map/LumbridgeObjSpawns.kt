package org.runerealm.content.areas.city.lumbridge.map

import org.runerealm.api.type.builders.map.obj.MapObjSpawnBuilder
import org.runerealm.content.areas.city.lumbridge.LumbridgeScript

object LumbridgeObjSpawns : MapObjSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<LumbridgeScript>("objs.toml")
    }
}
