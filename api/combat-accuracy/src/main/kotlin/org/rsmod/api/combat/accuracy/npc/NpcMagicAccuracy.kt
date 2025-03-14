package org.rsmod.api.combat.accuracy.npc

public object NpcMagicAccuracy {
    /**
     * @param effectiveMagic The effective magic level, as calculated by [calculateEffectiveMagic].
     * @see [calculateEffectiveMagic]
     */
    public fun calculateBaseAttackRoll(effectiveMagic: Int, magicBonus: Int): Int {
        return InternalNpcAccuracy.calculateBaseRoll(effectiveMagic, magicBonus)
    }

    /** @param visibleMagicLvl The visible magic level, including boosts or drains. */
    public fun calculateEffectiveMagic(visibleMagicLvl: Int): Int {
        return InternalNpcAccuracy.calculateEffectiveLevel(visibleMagicLvl)
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
