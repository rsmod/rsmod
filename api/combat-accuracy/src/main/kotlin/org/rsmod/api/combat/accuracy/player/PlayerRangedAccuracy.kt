package org.rsmod.api.combat.accuracy.player

import org.rsmod.api.combat.accuracy.safePercentScale
import org.rsmod.api.combat.accuracy.scaleByPercent

public object PlayerRangedAccuracy {
    /**
     * @param effectiveRanged The effective ranged level, as calculated by
     *   [calculateEffectiveRanged].
     * @see [calculateEffectiveRanged]
     */
    public fun calculateBaseAttackRoll(effectiveRanged: Int, rangedBonus: Int): Int {
        val effectiveBonus = rangedBonus + 64
        return effectiveRanged * effectiveBonus
    }

    /**
     * This function accepts `Double` values for certain modifiers but internally scales them to
     * integers for emulation accuracy.
     *
     * @param visibleRangedLvl The visible ranged level, including boosts (e.g., 112 if boosted by a
     *   ranging potion).
     * @param styleBonus The attack style bonus, including the base `+8` every style provides.
     *   (e.g., `Accurate` = `11`, `Rapid` = `8`, `Longrange` = `8`).
     * @param prayerBonus The prayer multiplier as a decimal (e.g., `1.20` for a `+20%` bonus).
     * @param voidBonus A multiplier applied when using Void equipment. (e.g., `1.1` when wearing
     *   full Ranged void or Elite ranged void armour).
     * @throws IllegalArgumentException if any multiplier is below `1.0`.
     */
    public fun calculateEffectiveRanged(
        visibleRangedLvl: Int,
        styleBonus: Int,
        prayerBonus: Double,
        voidBonus: Double,
    ): Int {
        require(prayerBonus >= 1.0) { "Prayer bonus must be greater or equal to 1." }
        require(voidBonus >= 1.0) { "Void bonus must be greater or equal to 1." }

        val prayerPercent = safePercentScale(prayerBonus)
        val voidPercent = safePercentScale(voidBonus)

        var effectiveLevel = visibleRangedLvl
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
     * @param visibleDefenceLvl The visible defence level, including boosts (e.g., 118 if boosted by
     *   a super defence potion).
     * @param styleBonus The attack style bonus, including the base `+8` every style provides.
     *   (e.g., `Defensive` = `11`, `Longrange` = `11`, `Controlled` = `9`, `Aggressive` = `8`).
     * @param prayerBonus The prayer multiplier as a decimal (e.g., `1.25` for a `+25%` bonus).
     * @param armourBonus A multiplier accounting for special armour effects (e.g., `1.05` for a
     *   `+5%` bonus). _Currently, only Torag's armour set passive should make use of this
     *   modifier._
     * @throws IllegalArgumentException if any multiplier is below `1.0`.
     */
    public fun calculateEffectiveDefence(
        visibleDefenceLvl: Int,
        styleBonus: Int,
        prayerBonus: Double,
        armourBonus: Double,
    ): Int {
        require(prayerBonus >= 1.0) { "Prayer bonus must be greater or equal to 1." }
        require(armourBonus >= 1.0) { "Armour bonus must be greater or equal to 1." }

        val prayerPercent = safePercentScale(prayerBonus)
        // This could be declared similar to `prayerPercent` for this use case, however we are
        // keeping it consistent with `weaponPercent` from melee max hit as they are similar
        // concepts.
        val armourPercent = safePercentScale(armourBonus - 1.0)

        var effectiveLevel = visibleDefenceLvl
        effectiveLevel = scaleByPercent(effectiveLevel, prayerPercent)
        effectiveLevel += scaleByPercent(effectiveLevel, armourPercent)
        effectiveLevel += styleBonus
        return effectiveLevel
    }
}
