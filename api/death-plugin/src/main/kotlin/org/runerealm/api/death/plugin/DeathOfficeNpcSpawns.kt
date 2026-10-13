package org.runerealm.api.death.plugin

import org.runerealm.api.type.builders.map.npc.MapNpcSpawnBuilder

internal object DeathOfficeNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<DeathOfficeScript>("npcs.toml")
    }
}
