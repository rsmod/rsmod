package org.rsmod.game.stat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.game.stat.PlayerSkillXPTable.XP_TABLE
import org.rsmod.game.stat.PlayerSkillXPTable.getLevelFromXP
import org.rsmod.game.stat.PlayerStatMap.Companion.MAX_XP

class PlayerSkillXPTableGetLevelTest {
    @Test
    fun `search level with exact level-up xp`() {
        val xp = 13_034_431
        val expectedLevel = 99
        val actualLevel = getLevelFromXP(xp)
        assertEquals(expectedLevel, actualLevel)
    }

    @Test
    fun `search level with xp just below level-up`() {
        val xp = 13_034_430
        val expectedLevel = 98
        val actualLevel = getLevelFromXP(xp)
        assertEquals(expectedLevel, actualLevel)
    }

    @Test
    fun `search level with max xp`() {
        val xp = MAX_XP
        val expectedLevel = XP_TABLE.size
        val actualLevel = getLevelFromXP(xp)
        assertEquals(expectedLevel, actualLevel)
    }

    @Test
    fun `search level with over max xp`() {
        val xp = MAX_XP + 100_000_000
        val expectedLevel = XP_TABLE.size
        val actualLevel = getLevelFromXP(xp)
        assertEquals(expectedLevel, actualLevel)
    }

    @Test
    fun `search level for exactly 0 xp`() {
        val xp = 0
        val expectedLevel = 1
        val actualLevel = getLevelFromXP(xp)
        assertEquals(expectedLevel, actualLevel)
    }

    @Test
    fun `search level for exactly 82 xp`() {
        val xp = 82
        val expectedLevel = 1
        val actualLevel = getLevelFromXP(xp)
        assertEquals(expectedLevel, actualLevel)
    }

    @Test
    fun `search level with non-exact xp`() {
        val xp = 105_000
        val expectedLevel = 50
        val actualLevel = getLevelFromXP(xp)
        assertEquals(expectedLevel, actualLevel)
    }
}
