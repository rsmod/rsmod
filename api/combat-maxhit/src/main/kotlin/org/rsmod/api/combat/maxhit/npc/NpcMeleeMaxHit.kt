package org.rsmod.api.combat.maxhit.npc

public object NpcMeleeMaxHit {
    /**
     * @param effectiveStrength The effective strength level, as calculated by
     *   [calculateEffectiveStrength].
     * @see [calculateEffectiveStrength]
     */
    public fun calculateBaseDamage(effectiveStrength: Int, strengthBonus: Int): Int {
        return InternalNpcMaxHits.calculateBaseDamage(effectiveStrength, strengthBonus)
    }

    /** @param visibleStrengthLvl The visible strength level, including boosts or drains. */
    public fun calculateEffectiveStrength(visibleStrengthLvl: Int): Int {
        return InternalNpcMaxHits.calculateEffectiveLevel(visibleStrengthLvl)
    }
}
