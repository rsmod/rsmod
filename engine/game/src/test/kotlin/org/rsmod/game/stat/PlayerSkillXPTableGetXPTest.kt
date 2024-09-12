package org.rsmod.game.stat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.game.stat.PlayerSkillXPTable.getXPFromLevel

class PlayerSkillXPTableGetXPTest {
    @Test
    fun `compute xp for level 1`() {
        val expectedXp = 0
        val actualXp = getXPFromLevel(1)
        assertEquals(expectedXp, actualXp)
    }

    @Test
    fun `compute xp for level 50`() {
        val expectedXp = 101_333
        val actualXp = getXPFromLevel(50)
        assertEquals(expectedXp, actualXp)
    }

    @Test
    fun `compute xp for level 99`() {
        val expectedXp = 13_034_431
        val actualXp = getXPFromLevel(99)
        assertEquals(expectedXp, actualXp)
    }

    @Test
    fun `compute xp for level 126`() {
        val expectedXp = 188_884_740
        val actualXp = getXPFromLevel(126)
        assertEquals(expectedXp, actualXp)
    }
}
