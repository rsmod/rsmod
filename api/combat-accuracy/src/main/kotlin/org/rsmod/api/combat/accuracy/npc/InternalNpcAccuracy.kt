package org.rsmod.api.combat.accuracy.npc

internal object InternalNpcAccuracy {
    fun calculateBaseRoll(effectiveLevel: Int, bonus: Int): Int {
        val effectiveBonus = bonus + 64
        return effectiveLevel * effectiveBonus
    }

    // As of writing this, `styleBonus` is always `9` for npcs (equivalent to `Controlled`).
    fun calculateEffectiveLevel(visibleLevel: Int, styleBonus: Int = 9): Int {
        return visibleLevel + styleBonus
    }
}
