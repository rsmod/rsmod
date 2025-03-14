package org.rsmod.api.combat.accuracy.npc

public object NpcMeleeAccuracy {
    /**
     * @param effectiveAttack The effective attack level, as calculated by
     *   [calculateEffectiveAttack].
     * @see [calculateEffectiveAttack]
     */
    public fun calculateBaseAttackRoll(effectiveAttack: Int, attackBonus: Int): Int {
        return InternalNpcAccuracy.calculateBaseRoll(effectiveAttack, attackBonus)
    }

    /** @param visibleAttackLvl The visible attack level, including boosts or drains. */
    public fun calculateEffectiveAttack(visibleAttackLvl: Int): Int {
        return InternalNpcAccuracy.calculateEffectiveLevel(visibleAttackLvl)
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
