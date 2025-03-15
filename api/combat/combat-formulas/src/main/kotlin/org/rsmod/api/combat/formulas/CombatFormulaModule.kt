package org.rsmod.api.combat.formulas

import org.rsmod.api.combat.formulas.accuracy.melee.PvNMeleeAccuracy
import org.rsmod.api.combat.formulas.attributes.collector.CombatNpcAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.DamageReductionAttributeCollector
import org.rsmod.api.combat.formulas.attributes.collector.MeleeWornAttributeCollector
import org.rsmod.api.combat.formulas.maxhit.melee.NvPMeleeMaxHit
import org.rsmod.api.combat.formulas.maxhit.melee.PvNMeleeMaxHit
import org.rsmod.plugin.module.PluginModule

public class CombatFormulaModule : PluginModule() {
    override fun bind() {
        bindInstance<CombatNpcAttributeCollector>()
        bindInstance<DamageReductionAttributeCollector>()
        bindInstance<MeleeWornAttributeCollector>()

        bindInstance<PvNMeleeAccuracy>()

        bindInstance<NvPMeleeMaxHit>()
        bindInstance<PvNMeleeMaxHit>()

        bindInstance<AccuracyFormulae>()
        bindInstance<MaxHitFormulae>()
    }
}
