package org.rsmod.api.combat.manager

import org.rsmod.plugin.module.PluginModule

public class CombatManagerModule : PluginModule() {
    override fun bind() {
        bindInstance<CombatChargeManager>()
        bindInstance<MagicRuneManager>()
        bindInstance<PlayerAttackManager>()
        bindInstance<RangedAmmoManager>()
    }
}
