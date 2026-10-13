package org.runerealm.api.combat.formulas

import org.runerealm.api.combat.formulas.accuracy.magic.NvNMagicAccuracy
import org.runerealm.api.combat.formulas.accuracy.magic.NvPMagicAccuracy
import org.runerealm.api.combat.formulas.accuracy.magic.PvNMagicAccuracy
import org.runerealm.api.combat.formulas.accuracy.magic.PvPMagicAccuracy
import org.runerealm.api.combat.formulas.accuracy.melee.NvNMeleeAccuracy
import org.runerealm.api.combat.formulas.accuracy.melee.NvPMeleeAccuracy
import org.runerealm.api.combat.formulas.accuracy.melee.PvNMeleeAccuracy
import org.runerealm.api.combat.formulas.accuracy.melee.PvPMeleeAccuracy
import org.runerealm.api.combat.formulas.accuracy.ranged.NvNRangedAccuracy
import org.runerealm.api.combat.formulas.accuracy.ranged.NvPRangedAccuracy
import org.runerealm.api.combat.formulas.accuracy.ranged.PvNRangedAccuracy
import org.runerealm.api.combat.formulas.accuracy.ranged.PvPRangedAccuracy
import org.runerealm.api.combat.formulas.attributes.collector.CombatMagicAttributeCollector
import org.runerealm.api.combat.formulas.attributes.collector.CombatMeleeAttributeCollector
import org.runerealm.api.combat.formulas.attributes.collector.CombatNpcAttributeCollector
import org.runerealm.api.combat.formulas.attributes.collector.CombatRangedAttributeCollector
import org.runerealm.api.combat.formulas.attributes.collector.DamageReductionAttributeCollector
import org.runerealm.api.combat.formulas.maxhit.magic.NvNMagicMaxHit
import org.runerealm.api.combat.formulas.maxhit.magic.NvPMagicMaxHit
import org.runerealm.api.combat.formulas.maxhit.magic.PvNMagicMaxHit
import org.runerealm.api.combat.formulas.maxhit.magic.PvPMagicMaxHit
import org.runerealm.api.combat.formulas.maxhit.melee.NvNMeleeMaxHit
import org.runerealm.api.combat.formulas.maxhit.melee.NvPMeleeMaxHit
import org.runerealm.api.combat.formulas.maxhit.melee.PvNMeleeMaxHit
import org.runerealm.api.combat.formulas.maxhit.melee.PvPMeleeMaxHit
import org.runerealm.api.combat.formulas.maxhit.ranged.NvNRangedMaxHit
import org.runerealm.api.combat.formulas.maxhit.ranged.NvPRangedMaxHit
import org.runerealm.api.combat.formulas.maxhit.ranged.PvNRangedMaxHit
import org.runerealm.api.combat.formulas.maxhit.ranged.PvPRangedMaxHit
import org.runerealm.plugin.module.PluginModule

public class CombatFormulaModule : PluginModule() {
    override fun bind() {
        bindInstance<CombatMagicAttributeCollector>()
        bindInstance<CombatMeleeAttributeCollector>()
        bindInstance<CombatNpcAttributeCollector>()
        bindInstance<CombatRangedAttributeCollector>()
        bindInstance<DamageReductionAttributeCollector>()

        bindInstance<AccuracyFormulae>()
        bindInstance<MaxHitFormulae>()

        bindInstance<NvNMagicAccuracy>()
        bindInstance<NvPMagicAccuracy>()
        bindInstance<PvNMagicAccuracy>()
        bindInstance<PvPMagicAccuracy>()

        bindInstance<NvNMagicMaxHit>()
        bindInstance<NvPMagicMaxHit>()
        bindInstance<PvNMagicMaxHit>()
        bindInstance<PvPMagicMaxHit>()

        bindInstance<NvNMeleeAccuracy>()
        bindInstance<NvPMeleeAccuracy>()
        bindInstance<PvNMeleeAccuracy>()
        bindInstance<PvPMeleeAccuracy>()

        bindInstance<NvNMeleeMaxHit>()
        bindInstance<NvPMeleeMaxHit>()
        bindInstance<PvNMeleeMaxHit>()
        bindInstance<PvPMeleeMaxHit>()

        bindInstance<NvNRangedAccuracy>()
        bindInstance<NvPRangedAccuracy>()
        bindInstance<PvNRangedAccuracy>()
        bindInstance<PvPRangedAccuracy>()

        bindInstance<NvNRangedMaxHit>()
        bindInstance<NvPRangedMaxHit>()
        bindInstance<PvNRangedMaxHit>()
        bindInstance<PvPRangedMaxHit>()
    }
}
