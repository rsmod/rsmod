package org.rsmod.api.combat.formulas.accuracy.multi

import jakarta.inject.Inject
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.accuracy.magic.NvPMagicAccuracy
import org.rsmod.api.combat.formulas.accuracy.melee.NvPMeleeAccuracy
import org.rsmod.api.combat.formulas.accuracy.ranged.NvPRangedAccuracy
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.UnpackedNpcType

public class NvPMultiStyleAccuracy
@Inject
constructor(
    private val magic: NvPMagicAccuracy,
    private val melee: NvPMeleeAccuracy,
    private val ranged: NvPRangedAccuracy,
) {
    public fun getMagicalMeleeHitChance(npc: Npc, target: Player): Int {
        return computeMagicalMeleeHitChance(source = npc, sourceType = npc.visType, target = target)
    }

    private fun computeMagicalMeleeHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: Player,
    ): Int {
        val attackRoll = melee.computeAttackRoll(source, sourceType)
        val defenceRoll = magic.computeDefenceRoll(target)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getRangedMeleeHitChance(npc: Npc, target: Player): Int {
        return computeRangedMeleeHitChance(source = npc, sourceType = npc.visType, target = target)
    }

    private fun computeRangedMeleeHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: Player,
    ): Int {
        val attackRoll = melee.computeAttackRoll(source, sourceType)
        val defenceRoll = ranged.computeDefenceRoll(target)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getRangedMagicHitChance(npc: Npc, target: Player): Int {
        return computeRangedMagicHitChance(source = npc, sourceType = npc.visType, target = target)
    }

    private fun computeRangedMagicHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: Player,
    ): Int {
        val attackRoll = magic.computeAttackRoll(source, sourceType)
        val defenceRoll = ranged.computeDefenceRoll(target)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun getMagicalRangedHitChance(npc: Npc, target: Player): Int {
        return computeMagicalRangedHitChance(
            source = npc,
            sourceType = npc.visType,
            target = target,
        )
    }

    private fun computeMagicalRangedHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: Player,
    ): Int {
        val attackRoll = ranged.computeAttackRoll(source, sourceType)
        val defenceRoll = magic.computeDefenceRoll(target)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }
}
