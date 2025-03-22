package org.rsmod.api.combat.formulas.accuracy.melee

import java.util.EnumSet
import org.rsmod.api.combat.accuracy.player.PlayerMeleeAccuracy
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.formulas.HIT_CHANCE_SCALE
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.attributes.CombatMeleeAttributes
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.stat.attackLvl
import org.rsmod.api.player.stat.defenceLvl
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.vars.VarPlayerIntMap

private typealias MeleeAttr = CombatMeleeAttributes

private typealias NpcAttr = CombatNpcAttributes

public object MeleeAccuracyOperations {
    public fun modifyAttackRoll(
        attackRoll: Int,
        meleeAttributes: EnumSet<CombatMeleeAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = attackRoll

        if (MeleeAttr.AmuletOfAvarice in meleeAttributes && NpcAttr.Revenant in npcAttributes) {
            val multiplier = if (MeleeAttr.ForinthrySurge in meleeAttributes) 27 else 24
            modified = scale(modified, multiplier, divisor = 20)
        } else if (MeleeAttr.SalveAmuletE in meleeAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        } else if (MeleeAttr.SalveAmulet in meleeAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 7, divisor = 6)
        } else if (MeleeAttr.BlackMask in meleeAttributes && NpcAttr.SlayerTask in npcAttributes) {
            modified = scale(modified, multiplier = 7, divisor = 6)
        }

        if (MeleeAttr.Obsidian in meleeAttributes && MeleeAttr.TzHaarWeapon in meleeAttributes) {
            modified += attackRoll / 10
        }

        if (MeleeAttr.RevenantWeapon in meleeAttributes && NpcAttr.Wilderness in npcAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        if (MeleeAttr.Arclight in meleeAttributes && NpcAttr.Demon in npcAttributes) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 149, divisor = 100)
                } else {
                    scale(modified, multiplier = 170, divisor = 100)
                }
        }

        if (MeleeAttr.BurningClaws in meleeAttributes && NpcAttr.Demon in npcAttributes) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 207, divisor = 200)
                } else {
                    scale(modified, multiplier = 210, divisor = 200)
                }
        }

        if (MeleeAttr.DragonHunterLance in meleeAttributes && NpcAttr.Draconic in npcAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        }

        if (MeleeAttr.DragonHunterWand in meleeAttributes && NpcAttr.Draconic in npcAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        if (MeleeAttr.KerisBreachPartisan in meleeAttributes && NpcAttr.Kalphite in npcAttributes) {
            modified = scale(modified, multiplier = 133, divisor = 100)
        }

        if (MeleeAttr.KerisSunPartisan in meleeAttributes && NpcAttr.Amascut in npcAttributes) {
            if (NpcAttr.QuarterHealth in npcAttributes) {
                modified = scale(modified, multiplier = 5, divisor = 4)
            }
        }

        // TODO(combat): Vampyre mods

        if (MeleeAttr.Crush in meleeAttributes) {
            var inquisitorPieces = 0
            if (MeleeAttr.InquisitorHelm in meleeAttributes) {
                inquisitorPieces++
            }
            if (MeleeAttr.InquisitorTop in meleeAttributes) {
                inquisitorPieces++
            }
            if (MeleeAttr.InquisitorBottom in meleeAttributes) {
                inquisitorPieces++
            }

            val multiplierAdditive =
                if (inquisitorPieces == 0) {
                    0
                } else if (MeleeAttr.InquisitorWeapon in meleeAttributes) {
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

    public fun modifyHitChance(
        hitChance: Int,
        attackRoll: Int,
        defenceRoll: Int,
        meleeAttributes: EnumSet<CombatMeleeAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = hitChance

        if (MeleeAttr.OsmumtensFang in meleeAttributes && MeleeAttr.Stab in meleeAttributes) {
            modified =
                if (NpcAttr.Amascut in npcAttributes) {
                    val scale = HIT_CHANCE_SCALE
                    scale - ((scale - hitChance) * (scale - hitChance) / scale)
                } else {
                    AccuracyOperations.calculateFangHitRoll(attackRoll, defenceRoll)
                }
        }

        return modified
    }

    public fun calculateEffectiveAttack(player: Player, attackStyle: MeleeAttackStyle?): Int =
        calculateEffectiveAttack(
            visLevel = player.attackLvl,
            vars = player.vars,
            worn = player.worn,
            attackStyle = attackStyle,
        )

    private fun calculateEffectiveAttack(
        visLevel: Int,
        vars: VarPlayerIntMap,
        worn: Inventory,
        attackStyle: MeleeAttackStyle?,
    ): Int {
        val styleBonus = attackStyle.offensiveStyleBonus()
        val prayerBonus = vars.offensivePrayerBonus()
        val voidBonus = worn.offensiveVoidBonus()
        return PlayerMeleeAccuracy.calculateEffectiveAttack(
            visibleAttackLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            voidBonus = voidBonus,
        )
    }

    private fun MeleeAttackStyle?.offensiveStyleBonus(): Int =
        when (this) {
            MeleeAttackStyle.Controlled -> 9
            MeleeAttackStyle.Accurate -> 11
            else -> 8
        }

    private fun VarPlayerIntMap.offensivePrayerBonus(): Double =
        when {
            this[varbits.clarity_of_thought] == 1 -> 1.05
            this[varbits.improved_reflexes] == 1 -> 1.1
            this[varbits.incredible_reflexes] == 1 -> 1.15
            this[varbits.chivalry] == 1 -> 1.15
            this[varbits.piety] == 1 -> 1.20
            else -> 1.0
        }

    private fun Inventory.offensiveVoidBonus(): Double {
        val helm = this[Wearpos.Hat.slot]
        if (!EquipmentChecks.isVoidMeleeHelm(helm)) {
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
