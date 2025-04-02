package org.rsmod.api.combat.formulas.accuracy.ranged

import org.rsmod.api.combat.accuracy.npc.NpcRangedAccuracy
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.config.refs.params
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.npc.UnpackedNpcType

public class NvNRangedAccuracy {
    public fun getHitChance(npc: Npc, target: Npc): Int =
        computeHitChance(
            source = npc,
            sourceType = npc.visType,
            target = target.visType,
            targetDefence = target.defenceLvl,
        )

    public fun computeHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: UnpackedNpcType,
        targetDefence: Int,
    ): Int {
        val attackRoll = computeAttackRoll(source, sourceType)
        val defenceRoll = computeDefenceRoll(target, targetDefence)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeAttackRoll(source: Npc, sourceType: UnpackedNpcType): Int {
        val effectiveRanged = NpcRangedAccuracy.calculateEffectiveRanged(source.rangedLvl)
        val rangedBonus = sourceType.param(params.attack_ranged)
        return NpcRangedAccuracy.calculateBaseAttackRoll(effectiveRanged, rangedBonus)
    }

    public fun computeDefenceRoll(target: UnpackedNpcType, targetDefence: Int): Int {
        val effectiveDefence = NpcRangedAccuracy.calculateEffectiveDefence(targetDefence)
        val defenceBonus = target.param(params.defence_ranged)
        return NpcRangedAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
    }
}
