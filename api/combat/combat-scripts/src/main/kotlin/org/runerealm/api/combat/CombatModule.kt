package org.runerealm.api.combat

import org.runerealm.plugin.module.PluginModule

internal class CombatModule : PluginModule() {
    override fun bind() {
        bindInstance<NvPCombat>()
        bindInstance<PvNCombat>()
        bindInstance<PvPCombat>()
    }
}
