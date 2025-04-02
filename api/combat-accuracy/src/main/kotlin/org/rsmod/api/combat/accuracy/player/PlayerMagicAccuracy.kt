package org.rsmod.api.combat.accuracy.player

import org.rsmod.api.combat.accuracy.safePercentScale
import org.rsmod.api.combat.accuracy.scaleByPercent

public object PlayerMagicAccuracy {
    /**
     * @param effectiveMagic The effective magic level, as calculated by [calculateEffectiveMagic].
     * @see [calculateEffectiveMagic]
     */
    public fun calculateBaseAttackRoll(effectiveMagic: Int, magicBonus: Int): Int {
        val effectiveBonus = magicBonus + 64
        return effectiveMagic * effectiveBonus
    }

    /**
     * This function accepts `Double` values for certain modifiers but internally scales them to
     * integers for emulation accuracy.
     *
     * @param visibleMagicLvl The visible magic level, including boosts (e.g., 103 if boosted by a
     *   magic potion).
     * @param styleBonus The attack style bonus, including a base `+9` every style provides. (e.g.,
     *   `Accurate` = `11`, Otherwise: `9`).
     * @param prayerBonus The prayer multiplier as a decimal (e.g., `1.25` for a `+25%` bonus).
     * @param voidBonus A multiplier applied when using Void equipment. (e.g., `1.45` when wearing
     *   full Magic void or Elite magic void armour).
     * @throws IllegalArgumentException if any multiplier is below `1.0`.
     */
    public fun calculateEffectiveMagic(
        visibleMagicLvl: Int,
        styleBonus: Int,
        prayerBonus: Double,
        voidBonus: Double,
    ): Int {
        require(prayerBonus >= 1.0) { "Prayer bonus must be greater or equal to 1." }
        require(voidBonus >= 1.0) { "Void bonus must be greater or equal to 1." }

        val prayerPercent = safePercentScale(prayerBonus)
        val voidPercent = safePercentScale(voidBonus)

        var effectiveLevel = visibleMagicLvl
        effectiveLevel = scaleByPercent(effectiveLevel, prayerPercent)
        effectiveLevel += styleBonus
        effectiveLevel = scaleByPercent(effectiveLevel, voidPercent)
        return effectiveLevel
    }

    /**
     * @param effectiveDefence The effective defence level, as calculated by
     *   [calculateEffectiveDefence].
     * @see [calculateEffectiveDefence]
     */
    public fun calculateBaseDefenceRoll(effectiveDefence: Int, defenceBonus: Int): Int {
        val effectiveBonus = defenceBonus + 64
        return effectiveDefence * effectiveBonus
    }

    /**
     * This function accepts `Double` values for certain modifiers but internally scales them to
     * integers for emulation accuracy.
     *
     * _Note: Unlike [PlayerMeleeAccuracy] and [PlayerRangedAccuracy], this function incorporates
     * both the player's magic level and a [magicPrayerBonus]. This is because magic defence rolls
     * take into account the target's magic level and an additional magic defence bonus from certain
     * prayers. The effective defence level is first adjusted based on the defence prayer and armour
     * bonuses. It is then combined with the scaled magic level using a 30% defence, 70% magic
     * split, before finally adding the style bonus._
     *
     * @param visibleDefenceLvl The visible defence level, including boosts (e.g., 118 if boosted by
     *   a super defence potion).
     * @param visibleMagicLvl The visible magic level, including boosts (e.g., 103 if boosted by a
     *   magic potion).
     * @param styleBonus The attack style bonus, including the base `+8` every style provides.
     *   (e.g., `Defensive` = `11`, `Longrange` = `11`, `Controlled` = `9`, `Aggressive` = `8`).
     * @param defencePrayerBonus The defence prayer multiplier as a decimal (e.g., `1.05` for Mystic
     *   vigour's `+5%` defence bonus).
     * @param magicPrayerBonus The magic defence prayer multiplier as a decimal (e.g., `1.18` for
     *   Mystic vigour's `+18%` magic defence bonus).
     * @param armourBonus A multiplier accounting for special armour effects (e.g., `1.05` for a
     *   `+5%` bonus). _Currently, only Torag's armour set passive should make use of this
     *   modifier._
     * @throws IllegalArgumentException if any multiplier is below `1.0`.
     */
    public fun calculateEffectiveDefence(
        visibleDefenceLvl: Int,
        visibleMagicLvl: Int,
        styleBonus: Int,
        defencePrayerBonus: Double,
        magicPrayerBonus: Double,
        armourBonus: Double,
    ): Int {
        require(defencePrayerBonus >= 1.0) { "Defence prayer bonus must be greater or equal to 1." }
        require(magicPrayerBonus >= 1.0) { "Magic prayer bonus must be greater or equal to 1." }
        require(armourBonus >= 1.0) { "Armour bonus must be greater or equal to 1." }

        val defPrayerPercent = safePercentScale(defencePrayerBonus)
        val armourPercent = safePercentScale(armourBonus - 1.0)
        var effectiveDefenceLevel = visibleDefenceLvl

        effectiveDefenceLevel = scaleByPercent(effectiveDefenceLevel, defPrayerPercent)
        effectiveDefenceLevel += scaleByPercent(effectiveDefenceLevel, armourPercent)

        val magicPrayerPercent = safePercentScale(magicPrayerBonus)
        var effectiveMagicLevel = visibleMagicLvl

        effectiveMagicLevel = scaleByPercent(effectiveMagicLevel, magicPrayerPercent)
        effectiveDefenceLevel = (effectiveMagicLevel * 7 / 10) + (effectiveDefenceLevel * 3 / 10)

        effectiveDefenceLevel += styleBonus
        return effectiveDefenceLevel
    }
}
