package org.rsmod.api.combat.accuracy.player

import org.rsmod.api.combat.accuracy.PERCENT_SCALE
import org.rsmod.api.combat.accuracy.scaleByPercent

public object PlayerMeleeAccuracy {
    /**
     * @param effectiveAttack The effective attack level, as calculated by
     *   [calculateEffectiveAttack].
     * @see [calculateEffectiveAttack]
     */
    public fun calculateBaseAttackRoll(effectiveAttack: Int, attackBonus: Int): Int {
        val effectiveBonus = attackBonus + 64
        return effectiveAttack * effectiveBonus
    }

    /**
     * This function accepts `Double` values for certain modifiers but internally scales them to
     * integers for emulation accuracy.
     *
     * @param visibleAttackLvl The visible attack level, including boosts (e.g., 118 if boosted by a
     *   super attack potion).
     * @param styleBonus The attack style bonus, including the base `+8` every style provides.
     *   (e.g., `Accurate` = `11`, `Controlled` = `9`, `Defensive` = `8`).
     * @param prayerBonus The prayer multiplier as a decimal (e.g., `1.20` for a `+20%` bonus).
     * @param voidBonus A multiplier applied when using Void equipment. (e.g., `1.1` when wearing
     *   full Melee void armour).
     * @throws IllegalArgumentException if any multiplier is below `1.0`.
     */
    public fun calculateEffectiveAttack(
        visibleAttackLvl: Int,
        styleBonus: Int,
        prayerBonus: Double,
        voidBonus: Double,
    ): Int {
        require(prayerBonus >= 1.0) { "Prayer bonus must be greater or equal to 1." }
        require(voidBonus >= 1.0) { "Void bonus must be greater or equal to 1." }

        val prayerPercent = (prayerBonus * PERCENT_SCALE).toInt()
        val voidPercent = (voidBonus * PERCENT_SCALE).toInt()

        var effectiveLevel = visibleAttackLvl
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

        val prayerPercent = (prayerBonus * PERCENT_SCALE).toInt()
        // This could be declared similar to `prayerPercent` for this use case, however we are
        // keeping it consistent with `weaponPercent` from melee max hit as they are similar
        // concepts.
        val armourPercent = ((armourBonus - 1.0) * PERCENT_SCALE).toInt()

        var effectiveLevel = visibleDefenceLvl
        effectiveLevel = scaleByPercent(effectiveLevel, prayerPercent)
        effectiveLevel += scaleByPercent(effectiveLevel, armourPercent)
        effectiveLevel += styleBonus
        return effectiveLevel
    }
}
