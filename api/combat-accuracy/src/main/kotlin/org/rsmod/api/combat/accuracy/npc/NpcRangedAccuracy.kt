package org.rsmod.api.combat.accuracy.npc

public object NpcRangedAccuracy {
    /**
     * @param effectiveRanged The effective ranged level, as calculated by
     *   [calculateEffectiveRanged].
     * @see [calculateEffectiveRanged]
     */
    public fun calculateBaseAttackRoll(effectiveRanged: Int, rangedBonus: Int): Int {
        return InternalNpcAccuracy.calculateBaseRoll(effectiveRanged, rangedBonus)
    }

    /** @param visibleRangedLvl The visible ranged level, including boosts or drains. */
    public fun calculateEffectiveRanged(visibleRangedLvl: Int): Int {
        return InternalNpcAccuracy.calculateEffectiveLevel(visibleRangedLvl)
    }

    /**
     * @param effectiveDefence The effective defence level, as calculated by
     *   [calculateEffectiveDefence].
     * @see [calculateEffectiveDefence]
     */
    public fun calculateBaseDefenceRoll(effectiveDefence: Int, defenceBonus: Int): Int {
        return InternalNpcAccuracy.calculateBaseRoll(effectiveDefence, defenceBonus)
    }

    /** @param visibleDefenceLvl The visible defence level, including boosts or drains. */
    public fun calculateEffectiveDefence(visibleDefenceLvl: Int): Int {
        return InternalNpcAccuracy.calculateEffectiveLevel(visibleDefenceLvl)
    }
}
