package org.rsmod.api.spells.attack

import org.rsmod.plugin.module.PluginModule

internal class SpellAttackModule : PluginModule() {
    override fun bind() {
        bindInstance<SpellAttackManager>()
        bindInstance<SpellAttackRegistry>()
        bindInstance<SpellAttackRepository>()

        newSetBinding<SpellAttackMap>()
    }
}
