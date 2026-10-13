package org.runerealm.api.weapons

import org.runerealm.plugin.module.PluginModule

public class WeaponModule : PluginModule() {
    override fun bind() {
        bindInstance<WeaponAttackManager>()
        bindInstance<WeaponRegistry>()
        bindInstance<WeaponRepository>()

        newSetBinding<WeaponMap>()
    }
}
