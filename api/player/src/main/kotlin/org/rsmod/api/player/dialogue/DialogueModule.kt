package org.rsmod.api.player.dialogue

import org.rsmod.plugin.module.PluginModule

public class DialogueModule : PluginModule() {
    override fun bind() {
        bindInstance<Dialogues>()
    }
}
