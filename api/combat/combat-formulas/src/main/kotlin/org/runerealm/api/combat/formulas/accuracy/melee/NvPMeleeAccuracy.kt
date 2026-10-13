package org.runerealm.api.combat.formulas.accuracy.melee

import jakarta.inject.Inject
import org.runerealm.api.combat.accuracy.npc.NpcMeleeAccuracy
import org.runerealm.api.combat.accuracy.player.PlayerMeleeAccuracy
import org.runerealm.api.combat.commons.types.MeleeAttackType
import org.runerealm.api.combat.formulas.accuracy.AccuracyOperations
import org.runerealm.api.combat.weapon.styles.AttackStyles
import org.runerealm.api.config.refs.params
import org.runerealm.api.player.bonus.WornBonuses
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.type.npc.UnpackedNpcType

public class NvPMeleeAccuracy
@Inject
constructor(private val bonuses: WornBonuses, private val attackStyles: AttackStyles) {
    public fun getHitChance(npc: Npc, target: Player, attackType: MeleeAttackType?): Int =
        computeHitChance(
            source = npc,
            sourceType = npc.visType,
            target = target,
            attackType = attackType,
        )

    public fun computeHitChance(
        source: Npc,
        sourceType: UnpackedNpcType,
        target: Player,
        attackType: MeleeAttackType?,
    ): Int {
        val attackRoll = computeAttackRoll(source, sourceType)
        val defenceRoll = computeDefenceRoll(target, attackType)
        return AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeAttackRoll(source: Npc, sourceType: UnpackedNpcType): Int {
        val effectiveAttack = NpcMeleeAccuracy.calculateEffectiveAttack(source.attackLvl)
        val attackBonus = sourceType.param(params.attack_melee)
        return NpcMeleeAccuracy.calculateBaseAttackRoll(effectiveAttack, attackBonus)
    }

    public fun computeDefenceRoll(target: Player, attackType: MeleeAttackType?): Int {
        val targetAttackStyle = attackStyles.get(target)
        val effectiveDefence =
            MeleeAccuracyOperations.calculateEffectiveDefence(target, targetAttackStyle)
        val defenceBonus = target.getDefenceBonus(attackType)
        return PlayerMeleeAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)
    }

    private fun Player.getDefenceBonus(attackType: MeleeAttackType?): Int =
        when (attackType) {
            MeleeAttackType.Stab -> bonuses.defensiveStabBonus(this)
            MeleeAttackType.Slash -> bonuses.defensiveSlashBonus(this)
            MeleeAttackType.Crush -> bonuses.defensiveCrushBonus(this)
            null -> 0
        }
}
