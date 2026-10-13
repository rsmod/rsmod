package org.runerealm.content.other.npcdrops

import org.runerealm.api.drop.table.NpcDropTableProvider
import org.runerealm.plugin.module.PluginModule

class NpcDropsModule : PluginModule() {
    override fun bind() {
        addSetBinding<NpcDropTableProvider>(LumbridgeNpcDrops::class.java)
    }
}
