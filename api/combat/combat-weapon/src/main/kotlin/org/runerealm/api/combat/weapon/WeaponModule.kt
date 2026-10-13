package org.runerealm.api.combat.weapon

import org.runerealm.api.combat.weapon.styles.AttackStyles
import org.runerealm.api.combat.weapon.types.AttackTypes
import org.runerealm.plugin.module.PluginModule

internal class WeaponModule : PluginModule() {
    override fun bind() {
        bindInstance<AttackStyles>()
        bindInstance<AttackTypes>()
    }
}
