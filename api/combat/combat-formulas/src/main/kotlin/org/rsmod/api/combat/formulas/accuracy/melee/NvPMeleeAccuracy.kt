package org.rsmod.api.combat.formulas.accuracy.melee

import jakarta.inject.Inject
import org.rsmod.api.combat.accuracy.npc.NpcMeleeAccuracy
import org.rsmod.api.combat.accuracy.player.PlayerMeleeAccuracy
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.npc.UnpackedNpcType

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
        return MeleeAccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
    }

    public fun computeAttackRoll(source: Npc, sourceType: UnpackedNpcType): Int {
        val effectiveAttack = NpcMeleeAccuracy.calculateEffectiveAttack(source.attackLvl)
        val attackBonus = sourceType.param(params.attack_melee)
        val attackRoll = NpcMeleeAccuracy.calculateBaseAttackRoll(effectiveAttack, attackBonus)
        return attackRoll
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
