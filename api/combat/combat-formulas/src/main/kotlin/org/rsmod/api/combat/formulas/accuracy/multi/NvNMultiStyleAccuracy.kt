package org.rsmod.api.combat.formulas.accuracy.multi

import jakarta.inject.Inject
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.accuracy.magic.NvNMagicAccuracy
import org.rsmod.api.combat.formulas.accuracy.melee.NvNMeleeAccuracy
import org.rsmod.api.combat.formulas.accuracy.ranged.NvNRangedAccuracy
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.npc.UnpackedNpcType

public class NvNMultiStyleAccuracy
@Inject
constructor(
    private val magic: NvNMagicAccuracy,
    private val melee: NvNMeleeAccuracy,
    private val ranged: NvNRangedAccuracy,
) {
    public fun getMagicalMeleeHitChance(npc: Npc, target: Npc): Int {
        return computeMagicalMeleeHitChance(
            source = npc,
            sourceType = npc.visType,
            target = target,
            targetType = target.visType,
        )
    }

    private fun computeMagicalMeleeHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: Npc,
        targetType: UnpackedNpcType,
    ): Int {
        val attackRoll = melee.computeAttackRoll(source, sourceType)
        val defenceRoll = magic.computeDefenceRoll(targetType, target.defenceLvl, target.magicLvl)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getRangedMeleeHitChance(npc: Npc, target: Npc): Int {
        return computeRangedMeleeHitChance(
            source = npc,
            sourceType = npc.visType,
            target = target.visType,
            targetDefence = target.defenceLvl,
        )
    }

    private fun computeRangedMeleeHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: UnpackedNpcType,
        targetDefence: Int,
    ): Int {
        val attackRoll = melee.computeAttackRoll(source, sourceType)
        val defenceRoll = ranged.computeDefenceRoll(target, targetDefence)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getRangedMagicHitChance(npc: Npc, target: Npc): Int {
        return computeRangedMagicHitChance(
            source = npc,
            sourceType = npc.visType,
            target = target.visType,
            targetDefence = target.defenceLvl,
        )
    }

    private fun computeRangedMagicHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: UnpackedNpcType,
        targetDefence: Int,
    ): Int {
        val attackRoll = magic.computeAttackRoll(source, sourceType)
        val defenceRoll = ranged.computeDefenceRoll(target, targetDefence)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getMagicalRangedHitChance(npc: Npc, target: Npc): Int {
        return computeMagicalRangedHitChance(
            source = npc,
            sourceType = npc.visType,
            target = target,
            targetType = target.visType,
        )
    }

    private fun computeMagicalRangedHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: Npc,
        targetType: UnpackedNpcType,
    ): Int {
        val attackRoll = ranged.computeAttackRoll(source, sourceType)
        val defenceRoll = magic.computeDefenceRoll(targetType, target.defenceLvl, target.magicLvl)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }
}
