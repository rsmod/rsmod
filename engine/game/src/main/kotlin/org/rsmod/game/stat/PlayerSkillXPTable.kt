package org.rsmod.game.stat

import kotlin.math.pow

public object PlayerSkillXPTable {
    public val XP_TABLE: IntArray =
        IntArray(126).apply {
            var accumulatedXp = 0
            for (level in 1 until size) {
                accumulatedXp += (level + 300 * 2.0.pow(level / 7.0)).toInt()
                this[level] = accumulatedXp / 4
            }
        }

    public fun getXPFromLevel(level: Int): Int {
        require(level - 1 in XP_TABLE.indices)
        return XP_TABLE[level - 1]
    }

    public fun getLevelFromXP(xp: Int): Int {
        val searchResultIndex = XP_TABLE.binarySearch(xp)
        if (searchResultIndex >= 0) {
            return searchResultIndex + 1
        }
        val nearestLowerLevel = -(searchResultIndex + 1)
        return nearestLowerLevel
    }

    public fun getFineXPFromLevel(level: Int): Int {
        val xp = getXPFromLevel(level)
        return PlayerStatMap.toFineXP(xp.toDouble()).toInt()
    }

    public fun getLevelFromFineXP(fineXp: Int): Int {
        val xp = PlayerStatMap.normalizeFineXP(fineXp)
        return getLevelFromXP(xp)
    }
}
