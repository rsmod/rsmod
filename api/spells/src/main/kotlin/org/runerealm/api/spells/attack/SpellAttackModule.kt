package org.runerealm.api.spells.attack

import org.runerealm.plugin.module.PluginModule

internal class SpellAttackModule : PluginModule() {
    override fun bind() {
        bindInstance<SpellAttackManager>()
        bindInstance<SpellAttackRegistry>()
        bindInstance<SpellAttackRepository>()

        newSetBinding<SpellAttackMap>()
    }
}
