package org.rsmod.api.dialogue

import org.rsmod.plugin.module.PluginModule

public class DialogueModule : PluginModule() {
    override fun bind() {
        bindInstance<Dialogues>()
    }
}
