package org.rsmod.api.combat.maxhit.player

import org.rsmod.api.combat.maxhit.safePercentScale
import org.rsmod.api.combat.maxhit.scaleByPercent

public object PlayerRangedMaxHit {
    /**
     * @param effectiveRanged The effective ranged level, as calculated by
     *   [calculateEffectiveRanged].
     * @see [calculateEffectiveRanged]
     */
    public fun calculateBaseDamage(effectiveRanged: Int, rangedStrengthBonus: Int): Int {
        val effectiveBonus = rangedStrengthBonus + 64
        return (effectiveRanged * effectiveBonus + 320) / 640
    }

    /**
     * This function accepts `Double` values for certain modifiers but internally scales them to
     * integers for emulation accuracy.
     *
     * @param visibleRangedLvl The visible ranged level, including boosts (e.g., 112 if boosted by a
     *   ranging potion).
     * @param styleBonus The attack style bonus, including the base `+8` every style provides.
     *   (e.g., `Accurate` = `11`, `Rapid` = `8`, `Longrange` = `8`).
     * @param prayerBonus The prayer multiplier as a decimal (e.g., `1.23` for a `+23%` bonus).
     * @param voidBonus A multiplier applied when using Void equipment. (e.g., `1.1` when wearing
     *   full Ranged void or `1.125` with full Elite ranged void armour).
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

        // Sharp eye has a special exception when the effective level is 20 or below.
        val sharpEyeException = effectiveLevel <= 20 && prayerBonus > 1.0 && prayerBonus <= 1.05

        if (sharpEyeException) {
            effectiveLevel += 1
        } else {
            effectiveLevel = scaleByPercent(effectiveLevel, prayerPercent)
        }

        effectiveLevel += styleBonus
        effectiveLevel = scaleByPercent(effectiveLevel, voidPercent)
        return effectiveLevel
    }
}
