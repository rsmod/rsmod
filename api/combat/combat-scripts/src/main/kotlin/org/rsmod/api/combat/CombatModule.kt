package org.rsmod.api.combat

import org.rsmod.plugin.module.PluginModule

internal class CombatModule : PluginModule() {
    override fun bind() {
        bindInstance<NpcCombat>()
        bindInstance<PlayerCombat>()
    }
}
