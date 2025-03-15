package org.rsmod.api.combat.formulas.accuracy.melee

import java.util.EnumSet
import kotlin.math.min
import kotlin.math.roundToInt
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatWornAttributes
import org.rsmod.api.combat.formulas.scale

private typealias WornAttr = CombatWornAttributes

private typealias NpcAttr = CombatNpcAttributes

public object MeleeAccuracyOperations {
    /**
     * The hit chance formulas ([calculateHitRoll] and [calculateFangHitRoll]) internally use
     * decimals (e.g., `1%` = `0.01`, `100%` = `1.0`). To maintain consistency with other combat
     * formulas that use whole integers, we scale them using this constant.
     */
    public const val HIT_CHANCE_SCALE: Int = 10_000

    public fun modifyBaseAttackRoll(
        attackRoll: Int,
        wornAttributes: EnumSet<CombatWornAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = attackRoll

        if (WornAttr.AmuletOfAvarice in wornAttributes && NpcAttr.Revenant in npcAttributes) {
            val multiplier = if (WornAttr.ForinthrySurge in wornAttributes) 27 else 24
            modified = scale(modified, multiplier, divisor = 20)
        } else if (WornAttr.SalveAmuletE in wornAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        } else if (WornAttr.SalveAmulet in wornAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 7, divisor = 6)
        } else if (WornAttr.BlackMask in wornAttributes && NpcAttr.SlayerTask in npcAttributes) {
            modified = scale(modified, multiplier = 7, divisor = 6)
        }

        if (WornAttr.Obsidian in wornAttributes && WornAttr.TzHaarWeapon in wornAttributes) {
            modified += attackRoll / 10
        }

        if (WornAttr.RevenantMeleeWeapon in wornAttributes && NpcAttr.Wilderness in npcAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        if (WornAttr.Arclight in wornAttributes && NpcAttr.Demon in npcAttributes) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 149, divisor = 100)
                } else {
                    scale(modified, multiplier = 170, divisor = 100)
                }
        }

        if (WornAttr.BurningClaws in wornAttributes && NpcAttr.Demon in npcAttributes) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 207, divisor = 200)
                } else {
                    scale(modified, multiplier = 210, divisor = 200)
                }
        }

        if (WornAttr.DragonHunterLance in wornAttributes && NpcAttr.Draconic in npcAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        }

        if (WornAttr.DragonHunterWand in wornAttributes && NpcAttr.Draconic in npcAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        if (WornAttr.KerisBreachPartisan in wornAttributes && NpcAttr.Kalphite in npcAttributes) {
            modified = scale(modified, multiplier = 133, divisor = 100)
        }

        if (WornAttr.KerisSunPartisan in wornAttributes && NpcAttr.Amascut in npcAttributes) {
            if (NpcAttr.QuarterHealth in npcAttributes) {
                modified = scale(modified, multiplier = 5, divisor = 4)
            }
        }

        // TODO(combat): Vampyre mods

        if (WornAttr.Crush in wornAttributes) {
            var inquisitorPieces = 0
            if (WornAttr.InquisitorHelm in wornAttributes) {
                inquisitorPieces++
            }
            if (WornAttr.InquisitorTop in wornAttributes) {
                inquisitorPieces++
            }
            if (WornAttr.InquisitorBottom in wornAttributes) {
                inquisitorPieces++
            }

            val multiplierAdditive =
                if (inquisitorPieces == 0) {
                    0
                } else if (WornAttr.InquisitorWeapon in wornAttributes) {
                    inquisitorPieces * 5
                } else if (inquisitorPieces == 3) {
                    5
                } else {
                    inquisitorPieces
                }

            if (multiplierAdditive > 0) {
                modified = scale(modified, multiplier = 200 + multiplierAdditive, divisor = 200)
            }
        }

        return modified
    }

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

    public fun modifyHitChance(
        hitChance: Int,
        attackRoll: Int,
        defenceRoll: Int,
        wornAttributes: EnumSet<CombatWornAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = hitChance

        if (WornAttr.OsmumtensFang in wornAttributes && WornAttr.Stab in wornAttributes) {
            modified =
                if (NpcAttr.Amascut in npcAttributes) {
                    val scale = HIT_CHANCE_SCALE
                    scale - ((scale - hitChance) * (scale - hitChance) / scale)
                } else {
                    calculateFangHitRoll(attackRoll, defenceRoll)
                }
        }

        return modified
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

    private fun calculateFangHitRoll(attackRoll: Int, defenceRoll: Int): Int {
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
}
