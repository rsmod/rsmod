package org.runerealm.api.specials

import org.runerealm.api.specials.energy.SpecialAttackEnergy
import org.runerealm.api.specials.weapon.SpecialAttackWeapons
import org.runerealm.plugin.module.PluginModule

public class SpecialAttackModule : PluginModule() {
    override fun bind() {
        bindInstance<SpecialAttackEnergy>()
        bindInstance<SpecialAttackManager>()
        bindInstance<SpecialAttackRegistry>()
        bindInstance<SpecialAttackRepository>()
        bindInstance<SpecialAttackWeapons>()

        newSetBinding<SpecialAttackMap>()
    }
}
