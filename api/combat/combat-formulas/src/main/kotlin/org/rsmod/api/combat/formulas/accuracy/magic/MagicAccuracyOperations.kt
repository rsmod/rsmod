package org.rsmod.api.combat.formulas.accuracy.magic

import java.util.EnumSet
import org.rsmod.api.combat.accuracy.player.PlayerMagicAccuracy
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.commons.styles.MagicAttackStyle
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatSpellAttributes
import org.rsmod.api.combat.formulas.attributes.CombatStaffAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.stat.defenceLvl
import org.rsmod.api.player.stat.magicLvl
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.vars.VarPlayerIntMap

private typealias SpellAttr = CombatSpellAttributes

private typealias StaffAttr = CombatStaffAttributes

private typealias NpcAttr = CombatNpcAttributes

public object MagicAccuracyOperations {
    /**
     * @param targetWeaknessPercent The target's elemental weakness percentage (e.g., `1` = `1%`).
     */
    public fun modifySpellAttackRoll(
        attackRoll: Int,
        targetWeaknessPercent: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = attackRoll

        var additiveBonus = 0
        var applyBlackMaskMod = false

        if (SpellAttr.AmuletOfAvarice in spellAttributes && NpcAttr.Revenant in npcAttributes) {
            additiveBonus += if (SpellAttr.ForinthrySurge in spellAttributes) 35 else 20
        } else if (SpellAttr.SalveAmuletEi in spellAttributes && NpcAttr.Undead in npcAttributes) {
            additiveBonus += 20
        } else if (SpellAttr.SalveAmuletI in spellAttributes && NpcAttr.Undead in npcAttributes) {
            additiveBonus += 15
        } else if (SpellAttr.BlackMaskI in spellAttributes && NpcAttr.SlayerTask in npcAttributes) {
            applyBlackMaskMod = true
        }

        // Note: Efaritay's aid mod is applied here, however, it seems that no actual magic
        // attack can count as a "silver weapon." Unsure if this is an oversight in dps
        // calculator, or if this is intentional. We will assume the latter for now.

        if (SpellAttr.SmokeStaff in spellAttributes && SpellAttr.StandardBook in spellAttributes) {
            additiveBonus += 10
        }

        modified = scale(modified, 100 + additiveBonus, 100)

        if (NpcAttr.Draconic in npcAttributes) {
            if (SpellAttr.DragonHunterLance in spellAttributes) {
                modified = scale(modified, multiplier = 6, divisor = 5)
            } else if (SpellAttr.DragonHunterWand in spellAttributes) {
                modified = scale(modified, multiplier = 3, divisor = 2)
            } else if (SpellAttr.DragonHunterCrossbow in spellAttributes) {
                modified = scale(modified, multiplier = 13, divisor = 10)
            }
        }

        if (applyBlackMaskMod) {
            modified = scale(modified, multiplier = 23, divisor = 20)
        }

        if (SpellAttr.Demonbane in spellAttributes && NpcAttr.Demon in npcAttributes) {
            var percent =
                if (NpcAttr.DemonbaneResistance in npcAttributes) {
                    if (SpellAttr.MarkOfDarkness in spellAttributes) 28 else 14
                } else {
                    if (SpellAttr.MarkOfDarkness in spellAttributes) 40 else 20
                }

            if (SpellAttr.PurgingStaff in spellAttributes) {
                percent *= 2
            }

            modified = scale(modified, 100 + percent, divisor = 100)
        }

        if (SpellAttr.RevenantWeapon in spellAttributes && NpcAttr.Revenant in npcAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        val applyWaterTomeMod =
            SpellAttr.WaterSpell in spellAttributes || SpellAttr.BindSpell in spellAttributes
        if (SpellAttr.WaterTome in spellAttributes && applyWaterTomeMod) {
            modified = scale(modified, multiplier = 6, divisor = 5)
        }

        // Note: In the dps calculator, the code below appears **after** the special attack
        // modifiers for Accursed sceptre and Volatile nightmare staff. However, when using
        // those specials, the player is not casting a spell - let alone one that matches the
        // npc's weakness. Therefore, these conditions should never overlap, and applying
        // these modifiers here should be safe.

        val applySpellWeaknessMod =
            NpcAttr.WindWeakness in npcAttributes && SpellAttr.WindSpell in spellAttributes ||
                NpcAttr.EarthWeakness in npcAttributes && SpellAttr.EarthSpell in spellAttributes ||
                NpcAttr.WaterWeakness in npcAttributes && SpellAttr.WaterSpell in spellAttributes ||
                NpcAttr.FireWeakness in npcAttributes && SpellAttr.FireSpell in spellAttributes

        if (applySpellWeaknessMod) {
            val additive = (attackRoll * (targetWeaknessPercent / 100.0)).toInt()
            modified += additive
        }

        // TODO(combat): Vampyre mods? Do magic spells have a mod if a silver staff weapon is used?

        return modified
    }

    public fun modifyStaffAttackRoll(
        attackRoll: Int,
        staffAttributes: EnumSet<CombatStaffAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = attackRoll

        var additiveBonus = 0
        var applyBlackMaskMod = false

        if (StaffAttr.AmuletOfAvarice in staffAttributes && NpcAttr.Revenant in npcAttributes) {
            additiveBonus += if (StaffAttr.ForinthrySurge in staffAttributes) 35 else 20
        } else if (StaffAttr.SalveAmuletEi in staffAttributes && NpcAttr.Undead in npcAttributes) {
            additiveBonus += 20
        } else if (StaffAttr.SalveAmuletI in staffAttributes && NpcAttr.Undead in npcAttributes) {
            additiveBonus += 15
        } else if (StaffAttr.BlackMaskI in staffAttributes && NpcAttr.SlayerTask in npcAttributes) {
            applyBlackMaskMod = true
        }

        modified = scale(modified, 100 + additiveBonus, 100)

        if (applyBlackMaskMod) {
            modified = scale(modified, multiplier = 23, divisor = 20)
        }

        if (StaffAttr.RevenantWeapon in staffAttributes && NpcAttr.Revenant in npcAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        return modified
    }

    public fun calculateEffectiveMagic(player: Player, attackStyle: MagicAttackStyle?): Int =
        calculateEffectiveMagic(
            visLevel = player.magicLvl,
            vars = player.vars,
            worn = player.worn,
            attackStyle = attackStyle,
        )

    private fun calculateEffectiveMagic(
        visLevel: Int,
        vars: VarPlayerIntMap,
        worn: Inventory,
        attackStyle: MagicAttackStyle?,
    ): Int {
        val styleBonus = attackStyle.offensiveStyleBonus()
        val prayerBonus = vars.offensivePrayerBonus()
        val voidBonus = worn.offensiveVoidBonus()
        return PlayerMagicAccuracy.calculateEffectiveMagic(
            visibleMagicLvl = visLevel,
            styleBonus = styleBonus,
            prayerBonus = prayerBonus,
            voidBonus = voidBonus,
        )
    }

    private fun MagicAttackStyle?.offensiveStyleBonus(): Int =
        when (this) {
            MagicAttackStyle.Accurate -> 11
            else -> 9
        }

    private fun VarPlayerIntMap.offensivePrayerBonus(): Double =
        when {
            this[varbits.mystic_will] == 1 -> 1.05
            this[varbits.mystic_lore] == 1 -> 1.1
            this[varbits.mystic_might] == 1 -> {
                if (this[varbits.mystic_vigour_unlocked] == 1) 1.18 else 1.15
            }
            this[varbits.augury] == 1 -> 1.25
            else -> 1.0
        }

    private fun Inventory.offensiveVoidBonus(): Double {
        val helm = this[Wearpos.Hat.slot]
        if (!EquipmentChecks.isVoidMageHelm(helm)) {
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

        return 1.45
    }

    public fun calculateEffectiveDefence(player: Player, attackStyle: AttackStyle?): Int {
        val armourBonus = AccuracyOperations.defensiveArmourBonus(player)
        return calculateEffectiveDefence(
            visDefenceLevel = player.defenceLvl,
            visMagicLevel = player.magicLvl,
            armourBonus = armourBonus,
            vars = player.vars,
            attackStyle = attackStyle,
        )
    }

    private fun calculateEffectiveDefence(
        visDefenceLevel: Int,
        visMagicLevel: Int,
        armourBonus: Double,
        vars: VarPlayerIntMap,
        attackStyle: AttackStyle?,
    ): Int {
        val styleBonus = AccuracyOperations.defensiveStyleBonus(attackStyle)
        val defencePrayerBonus = AccuracyOperations.defensivePrayerBonus(vars)
        val magicPrayerBonus = vars.magicDefencePrayerBonus()
        return PlayerMagicAccuracy.calculateEffectiveDefence(
            visibleDefenceLvl = visDefenceLevel,
            visibleMagicLvl = visMagicLevel,
            styleBonus = styleBonus,
            defencePrayerBonus = defencePrayerBonus,
            magicPrayerBonus = magicPrayerBonus,
            armourBonus = armourBonus,
        )
    }

    private fun VarPlayerIntMap.magicDefencePrayerBonus(): Double = offensivePrayerBonus()
}
