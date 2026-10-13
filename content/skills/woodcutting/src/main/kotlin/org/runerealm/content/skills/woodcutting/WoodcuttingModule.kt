package org.runerealm.content.skills.woodcutting

import org.runerealm.api.stats.levelmod.InvisibleLevelMod
import org.runerealm.plugin.module.PluginModule

class WoodcuttingModule : PluginModule() {
    override fun bind() {
        addSetBinding<InvisibleLevelMod>(WoodcuttingLevelBoosts::class.java)
    }
}
