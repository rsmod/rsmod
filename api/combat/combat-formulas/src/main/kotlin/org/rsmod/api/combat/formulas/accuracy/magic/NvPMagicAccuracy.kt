package org.rsmod.api.combat.formulas.accuracy.magic

import jakarta.inject.Inject
import org.rsmod.api.combat.accuracy.npc.NpcMagicAccuracy
import org.rsmod.api.combat.accuracy.player.PlayerMagicAccuracy
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.UnpackedNpcType

public class NvPMagicAccuracy
@Inject
constructor(private val bonuses: WornBonuses, private val attackStyles: AttackStyles) {
    public fun getHitChance(npc: Npc, target: Player): Int =
        computeHitChance(source = npc, sourceType = npc.visType, target = target)

    public fun computeHitChance(source: Npc, sourceType: UnpackedNpcType, target: Player): Int {
        val attackRoll = computeAttackRoll(source, sourceType)
        val defenceRoll = computeDefenceRoll(target)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeAttackRoll(source: Npc, sourceType: UnpackedNpcType): Int {
        val effectiveMagic = NpcMagicAccuracy.calculateEffectiveMagic(source.magicLvl)
        val magicBonus = sourceType.param(params.attack_magic)
        return NpcMagicAccuracy.calculateBaseAttackRoll(effectiveMagic, magicBonus)
    }

    public fun computeDefenceRoll(target: Player): Int {
        val targetAttackStyle = attackStyles.get(target)
        val effectiveDefence =
            MagicAccuracyOperations.calculateEffectiveDefence(target, targetAttackStyle)
        val defenceBonus = bonuses.defensiveMagicBonus(target)
        return PlayerMagicAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
    }
}
