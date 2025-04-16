package org.rsmod.api.combat.formulas.maxhit.ranged

import java.util.EnumSet
import kotlin.math.max
import kotlin.math.min
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatRangedAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.combat.maxhit.player.PlayerRangedMaxHit
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.stat.stat
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.vars.VarPlayerIntMap

private typealias RangeAttr = CombatRangedAttributes

private typealias NpcAttr = CombatNpcAttributes

public object RangedMaxHitOperations {
    /**
     * @param targetMagic The target's magic level or magic bonus, whichever of the two is greater.
     *   Required for the Twisted bow modifier.
     */
    public fun modifyBaseDamage(
        baseDamage: Int,
        targetMagic: Int,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = baseDamage

        if (RangeAttr.CrystalBow in rangeAttributes) {
            val helmAdditive = if (RangeAttr.CrystalHelm in rangeAttributes) 1 else 0
            val bodyAdditive = if (RangeAttr.CrystalBody in rangeAttributes) 3 else 0
            val legsAdditive = if (RangeAttr.CrystalLegs in rangeAttributes) 2 else 0
            val armourAdditive = helmAdditive + bodyAdditive + legsAdditive
            modified = scale(modified, multiplier = 40 + armourAdditive, divisor = 40)
        }

        var applyRevWeaponMod =
            RangeAttr.RevenantWeapon in rangeAttributes && NpcAttr.Wilderness in npcAttributes
        var applyDragonbaneMod =
            RangeAttr.DragonHunterCrossbow in rangeAttributes && NpcAttr.Draconic in npcAttributes
        var applyDemonbaneMod =
            RangeAttr.ScorchingBow in rangeAttributes && NpcAttr.Demon in npcAttributes

        if (RangeAttr.AmuletOfAvarice in rangeAttributes && NpcAttr.Revenant in npcAttributes) {
            val multiplier = if (RangeAttr.ForinthrySurge in rangeAttributes) 27 else 24
            modified = scale(modified, multiplier, divisor = 20)
        } else if (RangeAttr.SalveAmuletEi in rangeAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        } else if (RangeAttr.SalveAmuletI in rangeAttributes && NpcAttr.Undead in npcAttributes) {
            modified = scale(modified, multiplier = 7, divisor = 6)
        } else if (RangeAttr.BlackMaskI in rangeAttributes && NpcAttr.SlayerTask in npcAttributes) {
            var multiplier = 23

            if (applyRevWeaponMod) {
                applyRevWeaponMod = false
                multiplier += 10
            }

            if (applyDragonbaneMod) {
                applyDragonbaneMod = false
                multiplier += 5
            }

            if (applyDemonbaneMod) {
                applyDemonbaneMod = false
                multiplier += 6
            }

            modified = scale(modified, multiplier, divisor = 20)
        }

        if (RangeAttr.TwistedBow in rangeAttributes) {
            val cap = if (NpcAttr.Xerician in npcAttributes) 350 else 250
            val magic = min(cap, targetMagic)

            val factor = 14
            val base = 250

            val linearBonus = (3 * magic - factor) / 100
            val deviation = (3 * magic / 10) - (10 * factor)
            val quadraticPenalty = (deviation * deviation) / 100

            val multiplier = base + linearBonus - quadraticPenalty
            modified = scale(modified, multiplier, divisor = 100)
        }

        if (applyRevWeaponMod) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        if (applyDragonbaneMod) {
            modified = scale(modified, multiplier = 5, divisor = 4)
        }

        if (applyDemonbaneMod) {
            modified =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    scale(modified, multiplier = 121, divisor = 100)
                } else {
                    scale(modified, multiplier = 130, divisor = 100)
                }
        }

        if (RangeAttr.RatBoneWeapon in rangeAttributes && NpcAttr.Rat in npcAttributes) {
            modified += 10
        }

        return modified
    }

    public fun modifyBaseDamage(
        baseDamage: Int,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
    ): Int {
        var modified = baseDamage

        if (RangeAttr.CrystalBow in rangeAttributes) {
            val helmAdditive = if (RangeAttr.CrystalHelm in rangeAttributes) 1 else 0
            val bodyAdditive = if (RangeAttr.CrystalBody in rangeAttributes) 3 else 0
            val legsAdditive = if (RangeAttr.CrystalLegs in rangeAttributes) 2 else 0
            val armourAdditive = helmAdditive + bodyAdditive + legsAdditive
            modified = scale(modified, multiplier = 40 + armourAdditive, divisor = 40)
        }

        return modified
    }

    public fun modifyPostSpec(
        modifiedDamage: Int,
        boltSpecDamage: Int,
        attackRate: Int,
        rangeAttributes: EnumSet<CombatRangedAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = modifiedDamage

        val unshieldedTormentedDemon =
            RangeAttr.Heavy in rangeAttributes && NpcAttr.TormentedDemonUnshielded in npcAttributes
        if (unshieldedTormentedDemon) {
            val bonusDamage = max(0, (attackRate * attackRate) - 16)
            modified += bonusDamage
        }

        // TODO(combat): Vampyre mods

        modified += boltSpecDamage

        val corpBeastReduction =
            NpcAttr.CorporealBeast in npcAttributes && RangeAttr.CorpBaneWeapon !in rangeAttributes
        if (corpBeastReduction) {
            modified /= 2
        }

        return modified
    }

    public fun calculateEffectiveRanged(player: Player, attackStyle: RangedAttackStyle?): Int =
        calculateEffectiveRanged(
            visLevel = player.stat(stats.ranged),
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
        val styleBonus = attackStyle.styleBonus()
        val prayerBonus = vars.prayerBonus()
        val voidBonus = worn.voidBonus()
        return PlayerRangedMaxHit.calculateEffectiveRanged(
            visibleRangedLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            voidBonus = voidBonus,
        )
    }

    private fun RangedAttackStyle?.styleBonus(): Int =
        when (this) {
            RangedAttackStyle.Accurate -> 11
            else -> 8
        }

    private fun VarPlayerIntMap.prayerBonus(): Double =
        when {
            this[varbits.sharp_eye] == 1 -> 1.05
            this[varbits.hawk_eye] == 1 -> 1.1
            this[varbits.eagle_eye] == 1 -> {
                if (this[varbits.prayer_deadeye_unlocked] == 1) 1.18 else 1.15
            }
            this[varbits.rigour] == 1 -> 1.23
            else -> 1.0
        }

    private fun Inventory.voidBonus(): Double {
        val helm = this[Wearpos.Hat.slot]
        if (!EquipmentChecks.isVoidRangerHelm(helm)) {
            return 1.0
        }

        val gloves = this[Wearpos.Hands.slot]
        if (!EquipmentChecks.isVoidGloves(gloves)) {
            return 1.0
        }

        val top = this[Wearpos.Torso.slot]
        val legs = this[Wearpos.Legs.slot]

        if (EquipmentChecks.isEliteVoidTop(top) && EquipmentChecks.isEliteVoidRobe(legs)) {
            return 1.125
        }

        if (EquipmentChecks.isRegularVoidTop(top) && EquipmentChecks.isRegularVoidRobe(legs)) {
            return 1.1
        }

        return 1.0
    }
}
