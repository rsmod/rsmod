package org.rsmod.api.combat.formulas.maxhit.magic

import java.util.EnumSet
import kotlin.math.max
import org.rsmod.api.combat.formulas.attributes.CombatNpcAttributes
import org.rsmod.api.combat.formulas.attributes.CombatSpellAttributes
import org.rsmod.api.combat.formulas.attributes.CombatStaffAttributes
import org.rsmod.api.combat.formulas.scale
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.game.entity.Player

private typealias SpellAttr = CombatSpellAttributes

private typealias StaffAttr = CombatStaffAttributes

private typealias NpcAttr = CombatNpcAttributes

public object MagicMaxHitOperations {
    /**
     * @param sourceMagic The source's **current** magic level. Required for the Magic dart
     *   modifier.
     * @param sourceBaseMagicDmgBonus The source's base magic damage bonus as calculated by
     *   [WornBonuses.magicDamageBonusBase].
     * @param sourceMagicPrayerBonus The source's current prayer magic damage bonus as calculated by
     *   [getMagicDamagePrayerBonus].
     */
    public fun modifySpellBaseDamage(
        baseDamage: Int,
        sourceMagic: Int,
        sourceBaseMagicDmgBonus: Int,
        sourceMagicPrayerBonus: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = baseDamage

        if (SpellAttr.MagicDart in spellAttributes) {
            val slayerStaffBoost =
                SpellAttr.SlayerStaffE in spellAttributes && NpcAttr.SlayerTask in npcAttributes

            modified =
                if (slayerStaffBoost) {
                    13 + (sourceMagic / 6)
                } else {
                    10 + (sourceMagic / 10)
                }
        }

        if (SpellAttr.ChaosGauntlets in spellAttributes && SpellAttr.BoltSpell in spellAttributes) {
            modified += 3
        }

        if (SpellAttr.ChargeSpell in spellAttributes) {
            modified += 10
        }

        var modifiedMagicDmgBonus = sourceBaseMagicDmgBonus

        if (SpellAttr.SmokeStaff in spellAttributes && SpellAttr.StandardBook in spellAttributes) {
            modifiedMagicDmgBonus += 100
        }

        var applyBlackMaskMod = false

        if (SpellAttr.AmuletOfAvarice in spellAttributes && NpcAttr.Revenant in npcAttributes) {
            val additive = if (SpellAttr.ForinthrySurge in spellAttributes) 350 else 200
            modifiedMagicDmgBonus += additive
        } else if (SpellAttr.SalveAmuletEi in spellAttributes && NpcAttr.Undead in npcAttributes) {
            modifiedMagicDmgBonus += 200
        } else if (SpellAttr.SalveAmuletI in spellAttributes && NpcAttr.Undead in npcAttributes) {
            modifiedMagicDmgBonus += 150
        } else if (SpellAttr.BlackMaskI in spellAttributes && NpcAttr.SlayerTask in npcAttributes) {
            applyBlackMaskMod = true
        }

        modifiedMagicDmgBonus += sourceMagicPrayerBonus

        val maxAdditive = scale(modified, multiplier = modifiedMagicDmgBonus, divisor = 1000)

        modified += maxAdditive

        if (applyBlackMaskMod) {
            modified = scale(modified, multiplier = 23, divisor = 20)
        }

        if (NpcAttr.Draconic in npcAttributes) {
            if (SpellAttr.DragonHunterLance in spellAttributes) {
                modified = scale(modified, multiplier = 6, divisor = 5)
            } else if (SpellAttr.DragonHunterWand in spellAttributes) {
                modified = scale(modified, multiplier = 6, divisor = 5)
            } else if (SpellAttr.DragonHunterCrossbow in spellAttributes) {
                modified = scale(modified, multiplier = 5, divisor = 4)
            }
        }

        if (SpellAttr.RevenantWeapon in spellAttributes && NpcAttr.Wilderness in npcAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        return modified
    }

    /**
     * @param sourceMagic The source's **current** magic level. Required for the Magic dart
     *   modifier.
     * @param sourceBaseMagicDmgBonus The source's base magic damage bonus as calculated by
     *   [WornBonuses.magicDamageBonusBase].
     * @param sourceMagicPrayerBonus The source's current prayer magic damage bonus as calculated by
     *   [getMagicDamagePrayerBonus].
     */
    public fun modifySpellBaseDamage(
        baseDamage: Int,
        sourceMagic: Int,
        sourceBaseMagicDmgBonus: Int,
        sourceMagicPrayerBonus: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
    ): Int {
        var modified = baseDamage

        if (SpellAttr.MagicDart in spellAttributes) {
            modified = 10 + (sourceMagic / 10)
        }

        if (SpellAttr.ChaosGauntlets in spellAttributes && SpellAttr.BoltSpell in spellAttributes) {
            modified += 3
        }

        if (SpellAttr.ChargeSpell in spellAttributes) {
            modified += 10
        }

        var modifiedMagicDmgBonus = sourceBaseMagicDmgBonus

        if (SpellAttr.SmokeStaff in spellAttributes && SpellAttr.StandardBook in spellAttributes) {
            modifiedMagicDmgBonus += 100
        }

        modifiedMagicDmgBonus += sourceMagicPrayerBonus

        val maxAdditive = scale(modified, multiplier = modifiedMagicDmgBonus, divisor = 1000)

        modified += maxAdditive

        return modified
    }

    /**
     * @param baseDamage The initial base damage as input into [modifySpellBaseDamage]. Required for
     *   elemental weakness modifiers.
     * @param targetWeaknessPercent The target's elemental weakness percentage (e.g., `1` = `1%`).
     */
    public fun modifySpellDamageRange(
        modifiedDamage: Int,
        baseDamage: Int,
        attackRate: Int,
        targetWeaknessPercent: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): IntRange {
        var modifiedMin = 0
        var modifiedMax = modifiedDamage

        val applySpellWeaknessMod =
            NpcAttr.WindWeakness in npcAttributes && SpellAttr.WindSpell in spellAttributes ||
                NpcAttr.EarthWeakness in npcAttributes && SpellAttr.EarthSpell in spellAttributes ||
                NpcAttr.WaterWeakness in npcAttributes && SpellAttr.WaterSpell in spellAttributes ||
                NpcAttr.FireWeakness in npcAttributes && SpellAttr.FireSpell in spellAttributes

        if (applySpellWeaknessMod) {
            val additive = (baseDamage * (targetWeaknessPercent / 100.0)).toInt()
            modifiedMax += additive
        }

        if (SpellAttr.SunfireRunePassive in spellAttributes) {
            modifiedMin = modifiedMax / 10
        }

        val applyTomeMod =
            SpellAttr.EarthTome in spellAttributes && SpellAttr.EarthSpell in spellAttributes ||
                SpellAttr.WaterTome in spellAttributes && SpellAttr.WaterSpell in spellAttributes ||
                SpellAttr.FireTome in spellAttributes && SpellAttr.FireSpell in spellAttributes

        if (applyTomeMod) {
            modifiedMax = scale(modifiedMax, multiplier = 11, divisor = 10)
        }

        val applyMarkOfDarknessMod =
            SpellAttr.MarkOfDarkness in spellAttributes &&
                SpellAttr.Demonbane in spellAttributes &&
                NpcAttr.Demon in npcAttributes
        if (applyMarkOfDarknessMod) {
            val multiplier = if (SpellAttr.PurgingStaff in spellAttributes) 50 else 25
            modifiedMax = scale(modifiedMax, multiplier = 100 + multiplier, divisor = 100)
        }

        if (SpellAttr.AhrimPassive in spellAttributes) {
            modifiedMax = scale(modifiedMax, multiplier = 13, divisor = 10)
        }

        if (NpcAttr.TormentedDemonUnshielded in npcAttributes) {
            val bonusDamage = max(0, (attackRate * attackRate) - 16)
            modifiedMax += bonusDamage
        }

        return modifiedMin..modifiedMax
    }

    public fun modifySpellDamageRange(
        modifiedDamage: Int,
        spellAttributes: EnumSet<CombatSpellAttributes>,
    ): IntRange {
        var modifiedMin = 0
        var modifiedMax = modifiedDamage

        if (SpellAttr.SunfireRunePassive in spellAttributes) {
            modifiedMin = modifiedMax / 10
        }

        val applyTomeMod =
            SpellAttr.EarthTome in spellAttributes && SpellAttr.EarthSpell in spellAttributes ||
                SpellAttr.WaterTome in spellAttributes && SpellAttr.WaterSpell in spellAttributes ||
                SpellAttr.FireTome in spellAttributes && SpellAttr.FireSpell in spellAttributes

        if (applyTomeMod) {
            modifiedMax = scale(modifiedMax, multiplier = 12, divisor = 10)
        }

        if (SpellAttr.AhrimPassive in spellAttributes) {
            modifiedMax = scale(modifiedMax, multiplier = 13, divisor = 10)
        }

        return modifiedMin..modifiedMax
    }

    /**
     * @param sourceBaseMagicDmgBonus The source's base magic damage bonus as calculated by
     *   [WornBonuses.magicDamageBonusBase].
     * @param sourceMagicPrayerBonus The source's current prayer magic damage bonus as calculated by
     *   [getMagicDamagePrayerBonus].
     */
    public fun modifyStaffBaseDamage(
        baseDamage: Int,
        sourceBaseMagicDmgBonus: Int,
        sourceMagicPrayerBonus: Int,
        staffAttributes: EnumSet<CombatStaffAttributes>,
        npcAttributes: EnumSet<CombatNpcAttributes>,
    ): Int {
        var modified = baseDamage

        var modifiedMagicDmgBonus = sourceBaseMagicDmgBonus

        if (StaffAttr.TumekensShadow in staffAttributes) {
            val multiplier = if (NpcAttr.Amascut in npcAttributes) 4 else 3
            modifiedMagicDmgBonus *= multiplier
        }

        var applyBlackMaskMod = false

        if (StaffAttr.AmuletOfAvarice in staffAttributes && NpcAttr.Revenant in npcAttributes) {
            val additive = if (StaffAttr.ForinthrySurge in staffAttributes) 350 else 200
            modifiedMagicDmgBonus += additive
        } else if (StaffAttr.SalveAmuletEi in staffAttributes && NpcAttr.Undead in npcAttributes) {
            modifiedMagicDmgBonus += 200
        } else if (StaffAttr.SalveAmuletI in staffAttributes && NpcAttr.Undead in npcAttributes) {
            modifiedMagicDmgBonus += 150
        } else if (StaffAttr.BlackMaskI in staffAttributes && NpcAttr.SlayerTask in npcAttributes) {
            applyBlackMaskMod = true
        }

        modifiedMagicDmgBonus += sourceMagicPrayerBonus

        val maxAdditive = scale(modified, multiplier = modifiedMagicDmgBonus, divisor = 1000)

        modified += maxAdditive

        if (applyBlackMaskMod) {
            modified = scale(modified, multiplier = 23, divisor = 20)
        }

        if (StaffAttr.RevenantWeapon in staffAttributes && NpcAttr.Wilderness in npcAttributes) {
            modified = scale(modified, multiplier = 3, divisor = 2)
        }

        return modified
    }

    public fun modifyStaffBaseDamage(
        baseDamage: Int,
        sourceBaseMagicDmgBonus: Int,
        sourceMagicPrayerBonus: Int,
    ): Int {
        var modified = baseDamage

        var modifiedMagicDmgBonus = sourceBaseMagicDmgBonus
        modifiedMagicDmgBonus += sourceMagicPrayerBonus

        val maxAdditive = scale(modified, multiplier = modifiedMagicDmgBonus, divisor = 1000)

        modified += maxAdditive

        return modified
    }

    public fun getMagicDamagePrayerBonus(player: Player): Int =
        when {
            player.vars[varbits.mystic_lore] == 1 -> 10
            player.vars[varbits.mystic_might] == 1 -> {
                if (player.vars[varbits.mystic_vigour_unlocked] == 1) 30 else 20
            }
            player.vars[varbits.augury] == 1 -> 40
            else -> 0
        }
}
