package org.runerealm.content.other.social.clan

import org.runerealm.api.type.builders.map.npc.MapNpcSpawnBuilder

object ClanHubNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<ClanScript>("npcs.toml")
    }
}
