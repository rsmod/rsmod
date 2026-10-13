package org.runerealm.api.combat.formulas.accuracy.magic

import org.runerealm.api.combat.accuracy.npc.NpcMagicAccuracy
import org.runerealm.api.combat.formulas.accuracy.AccuracyOperations
import org.runerealm.api.config.refs.params
import org.runerealm.game.entity.Npc
import org.runerealm.game.type.npc.UnpackedNpcType

public class NvNMagicAccuracy {
    public fun getHitChance(npc: Npc, target: Npc): Int =
        computeHitChance(
            source = npc,
            sourceType = npc.visType,
            target = target,
            targetType = target.visType,
        )

    public fun computeHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: Npc,
        targetType: UnpackedNpcType,
    ): Int {
        val attackRoll = computeAttackRoll(source, sourceType)
        val defenceRoll = computeDefenceRoll(targetType, target.defenceLvl, target.magicLvl)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeAttackRoll(source: Npc, sourceType: UnpackedNpcType): Int {
        val effectiveMagic = NpcMagicAccuracy.calculateEffectiveMagic(source.magicLvl)
        val magicBonus = sourceType.param(params.attack_magic)
        return NpcMagicAccuracy.calculateBaseAttackRoll(effectiveMagic, magicBonus)
    }

    public fun computeDefenceRoll(
        target: UnpackedNpcType,
        targetDefence: Int,
        targetMagic: Int,
    ): Int {
        val defenceLevel =
            if (target.param(params.magic_defence_uses_defence_level)) {
                targetDefence
            } else {
                targetMagic
            }
        val effectiveDefence = NpcMagicAccuracy.calculateEffectiveDefence(defenceLevel)
        val defenceBonus = target.param(params.defence_magic)
        return NpcMagicAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
    }
}
