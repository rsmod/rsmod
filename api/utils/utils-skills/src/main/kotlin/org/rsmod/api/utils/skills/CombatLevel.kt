package org.rsmod.api.utils.skills

import kotlin.math.floor
import kotlin.math.max

public object CombatLevel {
    public fun calculate(
        attack: Int,
        defence: Int,
        strength: Int,
        hitpoints: Int,
        ranged: Int,
        magic: Int,
        prayer: Int,
    ): Int {
        val base = 0.25 * (defence + hitpoints + (prayer / 2))
        val melee = 0.325 * (attack + strength)
        val range = 0.325 * (ranged + (ranged / 2))
        val magic = 0.325 * (magic + (magic / 2))
        val combatMax = max(melee, max(range, magic))
        return floor(base + combatMax).toInt()
    }
}
