package org.rsmod.api.combat.maxhit.npc

public object NpcRangedMaxHit {
    /**
     * @param effectiveRanged The effective ranged level, as calculated by
     *   [calculateEffectiveRanged].
     * @see [calculateEffectiveRanged]
     */
    public fun calculateBaseDamage(effectiveRanged: Int, rangedStrengthBonus: Int): Int {
        return InternalNpcMaxHits.calculateBaseDamage(effectiveRanged, rangedStrengthBonus)
    }

    /** @param visibleRangedLvl The visible ranged level, including boosts or drains. */
    public fun calculateEffectiveRanged(visibleRangedLvl: Int): Int {
        return InternalNpcMaxHits.calculateEffectiveLevel(visibleRangedLvl)
    }
}
