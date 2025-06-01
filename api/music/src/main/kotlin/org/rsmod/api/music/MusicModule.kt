package org.rsmod.api.music

import org.rsmod.plugin.module.PluginModule

public class MusicModule : PluginModule() {
    override fun bind() {
        bindInstance<MusicRepository>()
    }
}
