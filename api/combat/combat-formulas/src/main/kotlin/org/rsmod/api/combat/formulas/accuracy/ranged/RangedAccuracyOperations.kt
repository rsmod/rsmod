package org.rsmod.api.combat.formulas.accuracy.ranged

import java.util.EnumSet
import kotlin.math.min
import org.rsmod.api.combat.accuracy.player.PlayerMeleeAccuracy
import org.rsmod.api.combat.accuracy.player.PlayerRangedAccuracy
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatRangedAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.stat.defenceLvl
import org.rsmod.api.player.stat.rangedLvl
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.vars.VarPlayerIntMap

private typealias RangeAttr = CombatRangedAttributes

private typealias NpcAttr = CombatNpcAttributes

public object RangedAccuracyOperations {
    /**
     * @param targetMagic The target's magic level or magic bonus, whichever of the two is greater.
     *   Required for the Twisted bow modifier.
     * @param targetDistance The chebyshev distance between the attacker's south-west coord and
     *   target's south-west coord. Required for the Chinchompa-fuse modifier.
     */
    public fun modifyAttackRoll(
        attackRoll: Int,
        targetMagic: Int,
        targetDistance: Int,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = attackRoll

        if (RangeAttr.CrystalBow in rangeAttributes) {
            val helmAdditive = if (RangeAttr.CrystalHelm in rangeAttributes) 1 else 0
            val bodyAdditive = if (RangeAttr.CrystalBody in rangeAttributes) 3 else 0
            val legsAdditive = if (RangeAttr.CrystalLegs in rangeAttributes) 2 else 0
            val armourAdditive = helmAdditive + bodyAdditive + legsAdditive
            modified = scale(modified, multiplier = 20 + armourAdditive, divisor = 20)
        }

        if (RangeAttr.AmuletOfAvarice in rangeAttributes && NpcAttr.Revenant in npcAttributes) {
            val multiplier = if (RangeAttr.ForinthrySurge in rangeAttributes) 27 else 24
            modified = scale(modified, multiplier, divisor = 20)
        } else if (RangeAttr.SalveAmuletEi in rangeAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        } else if (RangeAttr.SalveAmuletI in rangeAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 7, divisor = 6)
        } else if (RangeAttr.BlackMaskI in rangeAttributes && NpcAttr.SlayerTask in npcAttributes) {
            modified = scale(modified, multiplier = 23, divisor = 20)
        }

        if (RangeAttr.TwistedBow in rangeAttributes) {
            val cap = if (NpcAttr.Xerician in npcAttributes) 350 else 250
            val magic = min(cap, targetMagic)

            val factor = 10
            val base = 140

            val linearBonus = (3 * magic - factor) / 100
            val deviation = (3 * magic / 10) - (10 * factor)
            val quadraticPenalty = (deviation * deviation) / 100

            val multiplier = base + linearBonus - quadraticPenalty
            modified = scale(modified, multiplier, divisor = 100)
        }

        if (RangeAttr.RevenantWeapon in rangeAttributes && NpcAttr.Wilderness in npcAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        val dragonbaneMod =
            RangeAttr.DragonHunterCrossbow in rangeAttributes && NpcAttr.Draconic in npcAttributes
        if (dragonbaneMod) {
            modified = scale(modified, multiplier = 13, divisor = 10)
        }

        when {
            RangeAttr.ShortFuse in rangeAttributes -> {
                val multiplier =
                    when {
                        targetDistance >= 7 -> 2
                        targetDistance >= 4 -> 3
                        else -> 4
                    }
                modified = scale(modified, multiplier, divisor = 4)
            }

            RangeAttr.MediumFuse in rangeAttributes -> {
                val multiplier = if (targetDistance < 4 || targetDistance >= 7) 3 else 4
                modified = scale(modified, multiplier, divisor = 4)
            }

            RangeAttr.LongFuse in rangeAttributes -> {
                val multiplier =
                    when {
                        targetDistance < 4 -> 2
                        targetDistance < 7 -> 3
                        else -> 4
                    }
                modified = scale(modified, multiplier, divisor = 4)
            }
        }

        if (RangeAttr.ScorchingBow in rangeAttributes && NpcAttr.Demon in npcAttributes) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 121, divisor = 100)
                } else {
                    scale(modified, multiplier = 130, divisor = 100)
                }
        }

        return modified
    }

    public fun calculateEffectiveRanged(player: Player, attackStyle: RangedAttackStyle?): Int =
        calculateEffectiveRanged(
            visLevel = player.rangedLvl,
            vars = player.vars,
            worn = player.worn,
            attackStyle = attackStyle,
        )

    private fun calculateEffectiveRanged(
        visLevel: Int,
        vars: VarPlayerIntMap,
        worn: Inventory,
        attackStyle: RangedAttackStyle?,
    ): Int {
        val styleBonus = attackStyle.offensiveStyleBonus()
        val prayerBonus = vars.offensivePrayerBonus()
        val voidBonus = worn.offensiveVoidBonus()
        return PlayerRangedAccuracy.calculateEffectiveRanged(
            visibleRangedLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            voidBonus = voidBonus,
        )
    }

    private fun RangedAttackStyle?.offensiveStyleBonus(): Int =
        when (this) {
            RangedAttackStyle.Accurate -> 11
            else -> 8
        }

    private fun VarPlayerIntMap.offensivePrayerBonus(): Double =
        when {
            this[varbits.sharp_eye] == 1 -> 1.05
            this[varbits.hawk_eye] == 1 -> 1.1
            this[varbits.eagle_eye] == 1 -> {
                if (this[varbits.deadeye_unlocked] == 1) 1.18 else 1.15
            }
            this[varbits.rigour] == 1 -> 1.2
            else -> 1.0
        }

    private fun Inventory.offensiveVoidBonus(): Double {
        val helm = this[Wearpos.Hat.slot]
        if (!EquipmentChecks.isVoidRangerHelm(helm)) {
            return 1.0
        }

        val top = this[Wearpos.Torso.slot]
        if (!EquipmentChecks.isVoidTop(top)) {
            return 1.0
        }

        val legs = this[Wearpos.Legs.slot]
        if (!EquipmentChecks.isVoidRobe(legs)) {
            return 1.0
        }

        val gloves = this[Wearpos.Hands.slot]
        if (!EquipmentChecks.isVoidGloves(gloves)) {
            return 1.0
        }

        return 1.1
    }

    public fun calculateEffectiveDefence(player: Player, attackStyle: AttackStyle?): Int {
        val armourBonus = AccuracyOperations.defensiveArmourBonus(player)
        return calculateEffectiveDefence(
            visLevel = player.defenceLvl,
            armourBonus = armourBonus,
            vars = player.vars,
            attackStyle = attackStyle,
        )
    }

    private fun calculateEffectiveDefence(
        visLevel: Int,
        armourBonus: Double,
        vars: VarPlayerIntMap,
        attackStyle: AttackStyle?,
    ): Int {
        val styleBonus = AccuracyOperations.defensiveStyleBonus(attackStyle)
        val prayerBonus = AccuracyOperations.defensivePrayerBonus(vars)
        return PlayerMeleeAccuracy.calculateEffectiveDefence(
            visibleDefenceLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            armourBonus = armourBonus,
        )
    }
}
