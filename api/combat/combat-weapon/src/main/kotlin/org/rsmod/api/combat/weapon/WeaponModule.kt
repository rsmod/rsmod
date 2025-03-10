package org.rsmod.api.combat.weapon

import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.combat.weapon.types.AttackTypes
import org.rsmod.plugin.module.PluginModule

internal class WeaponModule : PluginModule() {
    override fun bind() {
        bindInstance<AttackStyles>()
        bindInstance<AttackTypes>()
    }
}
