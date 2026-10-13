package org.runerealm.api.player.dialogue

import org.runerealm.api.player.dialogue.align.TextAlignment
import org.runerealm.plugin.module.PluginModule

public class DialogueModule : PluginModule() {
    override fun bind() {
        bindInstance<TextAlignment>()
    }
}
