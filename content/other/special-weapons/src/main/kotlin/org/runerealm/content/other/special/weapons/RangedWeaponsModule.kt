package org.runerealm.content.other.special.weapons

import org.runerealm.api.weapons.WeaponMap
import org.runerealm.content.other.special.weapons.ranged.DarkBowWeapons
import org.runerealm.plugin.module.PluginModule

class RangedWeaponsModule : PluginModule() {
    override fun bind() {
        addSetBinding<WeaponMap>(DarkBowWeapons::class.java)
    }
}
