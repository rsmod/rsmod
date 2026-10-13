package org.runerealm.content.other.special.weapons

import org.runerealm.api.weapons.WeaponMap
import org.runerealm.content.other.special.weapons.magic.TumekensShadowWeapons
import org.runerealm.plugin.module.PluginModule

class MagicWeaponsModule : PluginModule() {
    override fun bind() {
        addSetBinding<WeaponMap>(TumekensShadowWeapons::class.java)
    }
}
