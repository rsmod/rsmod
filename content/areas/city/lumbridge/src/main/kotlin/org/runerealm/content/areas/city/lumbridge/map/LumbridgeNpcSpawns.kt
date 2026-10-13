package org.runerealm.content.areas.city.lumbridge.map

import org.runerealm.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.runerealm.content.areas.city.lumbridge.LumbridgeScript

object LumbridgeNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<LumbridgeScript>("npcs.toml")
    }
}
