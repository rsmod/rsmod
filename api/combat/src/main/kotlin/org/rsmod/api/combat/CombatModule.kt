package org.rsmod.api.combat

import org.rsmod.api.combat.styles.AttackStyles
import org.rsmod.plugin.module.PluginModule

internal class CombatModule : PluginModule() {
    override fun bind() {
        bindInstance<AttackStyles>()
    }
}
