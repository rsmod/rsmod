package org.rsmod.api.obj.spawns

import org.rsmod.plugin.module.PluginModule

public class ParsedObjSpawnerModule : PluginModule() {
    override fun bind() {
        bindInstance<ParsedObjSpawner>()
    }
}
