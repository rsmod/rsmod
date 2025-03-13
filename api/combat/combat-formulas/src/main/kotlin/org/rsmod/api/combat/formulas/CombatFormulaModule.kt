package org.rsmod.api.combat.formulas

import org.rsmod.api.combat.formulas.maxhit.melee.NvPMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.melee.PvNMeleeMaxHit
import org.rsmod.plugin.module.PluginModule

public class CombatFormulaModule : PluginModule() {
    override fun bind() {
        bindInstance<MaxHitFormulae>()
        bindInstance<NvPMeleeMaxHit>()
        bindInstance<PvNMeleeMaxHit>()
    }
}
