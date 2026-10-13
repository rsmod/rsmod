package org.runerealm.api.combat.manager

import org.runerealm.plugin.module.PluginModule

public class CombatManagerModule : PluginModule() {
    override fun bind() {
        bindInstance<CombatChargeManager>()
        bindInstance<MagicRuneManager>()
        bindInstance<PlayerAttackManager>()
        bindInstance<RangedAmmoManager>()
    }
}
