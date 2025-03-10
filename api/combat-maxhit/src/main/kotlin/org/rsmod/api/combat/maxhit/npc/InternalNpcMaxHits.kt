package org.rsmod.api.combat.maxhit.npc

internal object InternalNpcMaxHits {
    fun calculateBaseDamage(effectiveLevel: Int, bonus: Int): Int {
        val effectiveBonus = bonus + 64
        return (effectiveLevel * effectiveBonus + 320) / 640
    }

    // As of writing this, `styleBonus` is always `9` for npcs (equivalent to `Controlled`).
    fun calculateEffectiveLevel(visibleLevel: Int, styleBonus: Int = 9): Int {
        return visibleLevel + styleBonus
    }
}
