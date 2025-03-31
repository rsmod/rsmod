package org.rsmod.api.combat.formulas.accuracy

import java.util.EnumSet
import kotlin.math.min
import kotlin.math.roundToInt
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.formulas.HIT_CHANCE_SCALE
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.front
import org.rsmod.api.player.hat
import org.rsmod.api.player.legs
import org.rsmod.api.player.righthand
import org.rsmod.api.player.stat.baseHitpointsLvl
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.api.player.torso
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.isType
import org.rsmod.game.vars.VarPlayerIntMap

private typealias NpcAttr = CombatNpcAttributes

public object AccuracyOperations {
    /**
     * Calculates the hit chance based on the attacker's [attackRoll] and the target's
     * [defenceRoll].
     *
     * @return An integer between `0` and `10,000`, where `0` represents a `0%` hit chance, `1`
     *   represents a `0.01%` hit chance, and `10,000` represents a `100%` hit chance.
     */
    public fun calculateHitChance(attackRoll: Int, defenceRoll: Int): Int {
        return calculateHitRoll(attackRoll, defenceRoll)
    }

    private fun calculateHitRoll(attackRoll: Int, defenceRoll: Int): Int {
        fun stdRoll(attack: Double, defence: Double): Double {
            return if (attack > defence) {
                1 - ((defence + 2) / (2 * (attack + 1)))
            } else {
                attack / (2 * (defence + 1))
            }
        }

        var attack = attackRoll.toDouble()
        var defence = defenceRoll.toDouble()

        if (attack < 0) {
            attack = min(0.0, attack + 2)
        }

        if (defence < 0) {
            defence = min(0.0, defence + 2)
        }

        val result =
            when {
                attack >= 0 && defence >= 0 -> stdRoll(attack, defence)
                attack >= 0 && defence < 0 -> 1 - (1 / (-defence + 1) / (attack + 1))
                attack < 0 && defence >= 0 -> 0.0
                else -> stdRoll(-defence, -attack)
            }

        // The online dps calculator rounds the result instead of using flooring,
        // which is typically done in these combat formulas. Since our test cases are
        // based on its calculations, we match its behavior to maintain consistency.
        return (result * HIT_CHANCE_SCALE).roundToInt()
    }

    internal fun calculateFangHitRoll(attackRoll: Int, defenceRoll: Int): Int {
        fun stdRoll(attack: Double, defence: Double): Double {
            return if (attack > defence) {
                1 - ((defence + 2) * (2 * defence + 3) / ((attack + 1) * (attack + 1) * 6))
            } else {
                attack * (4 * attack + 5) / (6 * (attack + 1) * (defence + 1))
            }
        }

        fun rvRoll(attack: Double, defence: Double): Double {
            return if (attack < defence) {
                attack * (defence * 6 - 2 * attack + 5) / (6 * (defence + 1) * (defence + 1))
            } else {
                1 - ((defence + 2) * (2 * defence + 3) / (6 * (defence + 1) * (attack + 1)))
            }
        }

        var attack = attackRoll.toDouble()
        var defence = defenceRoll.toDouble()

        if (attack < 0) {
            attack = min(0.0, attack + 2)
        }

        if (defence < 0) {
            defence = min(0.0, defence + 2)
        }

        val result =
            when {
                attack >= 0 && defence >= 0 -> stdRoll(attack, defence)
                attack >= 0 && defence < 0 -> 1 - (1 / (-defence + 1) / (attack + 1))
                attack < 0 && defence >= 0 -> 0.0
                else -> rvRoll(-defence, -attack)
            }

        // The online dps calculator rounds the result instead of using flooring,
        // which is typically done in these combat formulas. Since our test cases are
        // based on its calculations, we match its behavior to maintain consistency.
        return (result * HIT_CHANCE_SCALE).roundToInt()
    }

    internal fun modifyDefenceRoll(
        defenceRoll: Int,
        amascutInvocationLvl: Int,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = defenceRoll

        if (NpcAttr.Amascut in npcAttributes) {
            modified = scale(modified, multiplier = 250 + amascutInvocationLvl, divisor = 250)
        }

        return modified
    }

    internal fun defensiveArmourBonus(player: Player): Double {
        if (!player.front.isType(objs.amulet_of_the_damned_full)) {
            return 1.0
        }

        val toragsSet =
            EquipmentChecks.isToragSet(player.hat, player.torso, player.legs, player.righthand)
        if (!toragsSet) {
            return 1.0
        }

        val currHitpoints = player.hitpoints
        val maxHitpoints = player.baseHitpointsLvl
        val missingPercent = (maxHitpoints - currHitpoints).toDouble() / maxHitpoints

        return 1.0 + missingPercent
    }

    internal fun defensiveStyleBonus(attackStyle: AttackStyle?): Int =
        when (attackStyle) {
            AttackStyle.LongrangeRanged -> 11
            AttackStyle.DefensiveMelee -> 11
            AttackStyle.ControlledMelee -> 9
            else -> 8
        }

    internal fun defensivePrayerBonus(vars: VarPlayerIntMap): Double =
        when {
            vars[varbits.piety] == 1 -> 1.25
            vars[varbits.rigour] == 1 -> 1.25
            vars[varbits.augury] == 1 -> 1.25
            vars[varbits.chivalry] == 1 -> 1.20
            vars[varbits.steel_skin] == 1 -> 1.15
            vars[varbits.rock_skin] == 1 -> 1.10
            vars[varbits.thick_skin] == 1 -> 1.05
            vars[varbits.hawk_eye] == 1 && vars[varbits.deadeye_unlocked] == 1 -> 1.05
            vars[varbits.mystic_might] == 1 && vars[varbits.mystic_vigour_unlocked] == 1 -> 1.05
            else -> 1.0
        }
}
