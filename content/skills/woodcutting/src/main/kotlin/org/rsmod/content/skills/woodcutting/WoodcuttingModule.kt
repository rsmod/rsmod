package org.rsmod.content.skills.woodcutting

import org.rsmod.api.xpmod.XpMod
import org.rsmod.plugin.module.PluginModule

class WoodcuttingModule : PluginModule() {
    override fun bind() {
        addSetBinding<XpMod>(WoodcuttingXpMod::class.java)
    }
}
