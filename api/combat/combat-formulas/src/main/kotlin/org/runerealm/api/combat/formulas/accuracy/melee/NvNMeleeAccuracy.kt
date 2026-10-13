package org.runerealm.api.combat.formulas.accuracy.melee

import org.runerealm.api.combat.accuracy.npc.NpcMeleeAccuracy
import org.runerealm.api.combat.commons.types.MeleeAttackType
import org.runerealm.api.combat.formulas.accuracy.AccuracyOperations
import org.runerealm.api.config.refs.params
import org.runerealm.game.entity.Npc
import org.runerealm.game.type.npc.UnpackedNpcType

public class NvNMeleeAccuracy {
    public fun getHitChance(npc: Npc, target: Npc, attackType: MeleeAttackType?): Int =
        computeHitChance(
            source = npc,
            sourceType = npc.visType,
            target = target,
            targetType = target.visType,
            attackType = attackType,
        )

    public fun computeHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: Npc,
        targetType: UnpackedNpcType,
        attackType: MeleeAttackType?,
    ): Int {
        val attackRoll = computeAttackRoll(source, sourceType)
        val defenceRoll = computeDefenceRoll(targetType, target.defenceLvl, attackType)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeAttackRoll(source: Npc, sourceType: UnpackedNpcType): Int {
        val effectiveAttack = NpcMeleeAccuracy.calculateEffectiveAttack(source.attackLvl)
        val attackBonus = sourceType.param(params.attack_melee)
        return NpcMeleeAccuracy.calculateBaseAttackRoll(effectiveAttack, attackBonus)
    }

    public fun computeDefenceRoll(
        target: UnpackedNpcType,
        targetDefence: Int,
        blockType: MeleeAttackType?,
    ): Int {
        val effectiveDefence = NpcMeleeAccuracy.calculateEffectiveDefence(targetDefence)
        val defenceBonus = target.getDefenceBonus(blockType)
        return NpcMeleeAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
    }

    private fun UnpackedNpcType.getDefenceBonus(attackType: MeleeAttackType?): Int =
        when (attackType) {
            MeleeAttackType.Stab -> param(params.defence_stab)
            MeleeAttackType.Slash -> param(params.defence_slash)
            MeleeAttackType.Crush -> param(params.defence_crush)
            null -> 0
        }
}
