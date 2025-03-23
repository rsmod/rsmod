package org.rsmod.api.weapons

import org.rsmod.plugin.module.PluginModule

public class WeaponModule : PluginModule() {
    override fun bind() {
        bindInstance<WeaponManager>()
        bindInstance<WeaponRegistry>()
        bindInstance<WeaponRepository>()

        newSetBinding<WeaponMap>()
    }
}
