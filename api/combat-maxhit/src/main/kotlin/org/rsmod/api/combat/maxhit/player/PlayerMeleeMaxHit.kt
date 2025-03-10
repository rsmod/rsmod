package org.rsmod.api.combat.maxhit.player

import org.rsmod.api.combat.maxhit.PERCENT_SCALE
import org.rsmod.api.combat.maxhit.scaleByPercent

public object PlayerMeleeMaxHit {
    /**
     * @param effectiveStrength The effective strength level, as calculated by
     *   [calculateEffectiveStrength].
     * @see [calculateEffectiveStrength]
     */
    public fun calculateBaseDamage(effectiveStrength: Int, strengthBonus: Int): Int {
        val effectiveBonus = strengthBonus + 64
        return (effectiveStrength * effectiveBonus + 320) / 640
    }

    /**
     * This function accepts `Double` values for certain modifiers but internally scales them to
     * integers for emulation accuracy.
     *
     * @param visibleStrengthLvl The visible strength level, including boosts (e.g., 118 if boosted
     *   by a super strength potion).
     * @param styleBonus The attack style bonus, including the base `+8` every style provides.
     *   (e.g., `Aggressive` = `11`, `Controlled` = `9`, `Defensive` = `8`).
     * @param prayerBonus The prayer multiplier as a decimal (e.g., `1.23` for a `+23%` bonus).
     * @param weaponBonus A multiplier accounting for special weapon effects (e.g., `1.12` for two
     *   Soulreaper soul stacks, where each stack adds `+6%`). _Currently, only the Soulreaper axe
     *   should make use of this modifier._
     * @param voidBonus A multiplier applied when using Void equipment. (e.g., `1.1` when wearing
     *   full Melee void armour).
     * @throws IllegalArgumentException if any multiplier is below `1.0`.
     */
    public fun calculateEffectiveStrength(
        visibleStrengthLvl: Int,
        styleBonus: Int,
        prayerBonus: Double,
        voidBonus: Double,
        weaponBonus: Double,
    ): Int {
        require(prayerBonus >= 1.0) { "Prayer bonus must be greater or equal to 1." }
        require(voidBonus >= 1.0) { "Void bonus must be greater or equal to 1." }
        require(weaponBonus >= 1.0) { "Weapon bonus must be greater or equal to 1." }

        val prayerPercent = (prayerBonus * PERCENT_SCALE).toInt()
        val voidPercent = (voidBonus * PERCENT_SCALE).toInt()
        val weaponPercent = ((weaponBonus - 1.0) * PERCENT_SCALE).toInt()

        var effectiveLevel = visibleStrengthLvl

        // Burst of strength has a special exception when the effective level is 20 or below.
        val burstOfStrengthException =
            effectiveLevel <= 20 && prayerBonus > 1.0 && prayerBonus <= 1.05

        if (burstOfStrengthException) {
            effectiveLevel += 1
        } else {
            effectiveLevel = scaleByPercent(effectiveLevel, prayerPercent)
        }

        // `weaponPercent` calculation is based off the base `visibleStrengthLevel`.
        effectiveLevel += scaleByPercent(visibleStrengthLvl, weaponPercent)
        effectiveLevel += styleBonus
        effectiveLevel = scaleByPercent(effectiveLevel, voidPercent)
        return effectiveLevel
    }
}
