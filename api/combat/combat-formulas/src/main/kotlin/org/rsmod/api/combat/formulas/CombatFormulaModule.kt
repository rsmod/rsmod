package org.rsmod.api.combat.formulas

import org.rsmod.api.combat.formulas.maxhit.MeleeMaxHit
import org.rsmod.plugin.module.PluginModule

public class CombatFormulaModule : PluginModule() {
    override fun bind() {
        bindInstance<MeleeMaxHit>()
        bindInstance<MaxHitFormulas>()
    }
}
