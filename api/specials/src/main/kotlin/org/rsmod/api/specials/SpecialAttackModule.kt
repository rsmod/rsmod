package org.rsmod.api.specials

import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.api.specials.weapon.SpecialAttackWeapons
import org.rsmod.plugin.module.PluginModule

public class SpecialAttackModule : PluginModule() {
    override fun bind() {
        bindInstance<SpecialAttackEnergy>()
        bindInstance<SpecialAttackRegistry>()
        bindInstance<SpecialAttackRepository>()
        bindInstance<SpecialAttackWeapons>()
        newSetBinding<SpecialAttackMap>()
    }
}
