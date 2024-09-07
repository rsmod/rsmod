package org.rsmod.api.npc.spawn

import org.rsmod.plugin.module.PluginModule

public class ParsedNpcSpawnerModule : PluginModule() {
    override fun bind() {
        bindInstance<ParsedNpcSpawner>()
    }
}
