package org.runerealm.api.music

import org.runerealm.plugin.module.PluginModule

public class MusicModule : PluginModule() {
    override fun bind() {
        bindInstance<MusicRepository>()
    }
}
