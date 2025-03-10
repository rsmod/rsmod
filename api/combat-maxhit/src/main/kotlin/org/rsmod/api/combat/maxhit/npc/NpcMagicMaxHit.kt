package org.rsmod.api.combat.maxhit.npc

public object NpcMagicMaxHit {
    /**
     * @param effectiveMagic The effective magic level, as calculated by [calculateEffectiveMagic].
     * @see [calculateEffectiveMagic]
     */
    public fun calculateBaseDamage(effectiveMagic: Int, magicStrengthBonus: Int): Int {
        return InternalNpcMaxHits.calculateBaseDamage(effectiveMagic, magicStrengthBonus)
    }

    /** @param visibleMagicLvl The visible magic level, including boosts or drains. */
    public fun calculateEffectiveMagic(visibleMagicLvl: Int): Int {
        return InternalNpcMaxHits.calculateEffectiveLevel(visibleMagicLvl)
    }
}
