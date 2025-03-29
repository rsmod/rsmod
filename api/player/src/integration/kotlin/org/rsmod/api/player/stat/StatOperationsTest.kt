package org.rsmod.api.player.stat

import org.junit.jupiter.api.Test
import org.rsmod.api.config.refs.stats
import org.rsmod.api.testing.GameTestState

class StatOperationsTest {
    @Test
    fun GameTestState.`statSub uses base stat level for percent drain`() = runGameTest {
        player.setBaseLevel(stats.hitpoints, 99)
        player.setCurrentLevel(stats.hitpoints, 10)

        player.statSub(stats.hitpoints, constant = 0, percent = 10)
        assertEquals(1, player.hitpoints)
    }

    @Test
    fun GameTestState.`statSub cumulatively decreases over multiple calls`() = runGameTest {
        player.setBaseLevel(stats.hitpoints, 99)
        player.setCurrentLevel(stats.hitpoints, 10)

        player.statSub(stats.hitpoints, constant = 3, percent = 0)
        assertEquals(7, player.hitpoints)

        player.statSub(stats.hitpoints, constant = 2, percent = 0)
        assertEquals(5, player.hitpoints)

        player.statSub(stats.hitpoints, constant = 1, percent = 0)
        assertEquals(4, player.hitpoints)
    }

    @Test
    fun GameTestState.`statSub caps at 0`() = runGameTest {
        player.setBaseLevel(stats.hitpoints, 99)
        player.setCurrentLevel(stats.hitpoints, 99)

        player.statSub(stats.hitpoints, constant = 0, percent = 100)
        assertEquals(0, player.hitpoints)

        player.statSub(stats.hitpoints, constant = 100, percent = 0)
        assertEquals(0, player.hitpoints)

        player.statSub(stats.hitpoints, constant = 0, percent = 100)
        assertEquals(0, player.hitpoints)

        player.statSub(stats.hitpoints, constant = 100, percent = 100)
        assertEquals(0, player.hitpoints)
    }

    @Test
    fun GameTestState.`statAdd uses base stat level for percent boost`() = runGameTest {
        player.setBaseLevel(stats.hitpoints, 99)
        player.setCurrentLevel(stats.hitpoints, 1)

        player.statAdd(stats.hitpoints, constant = 0, percent = 100)
        assertEquals(100, player.hitpoints)
    }

    @Test
    fun GameTestState.`statAdd accumulates over multiple calls`() = runGameTest {
        player.setBaseLevel(stats.hitpoints, 99)
        player.setCurrentLevel(stats.hitpoints, 1)

        player.statAdd(stats.hitpoints, constant = 5, percent = 0)
        assertEquals(6, player.hitpoints)

        player.statAdd(stats.hitpoints, constant = 10, percent = 0)
        assertEquals(16, player.hitpoints)
    }

    @Test
    fun GameTestState.`statAdd caps at 255`() = runGameTest {
        player.setBaseLevel(stats.hitpoints, 99)
        player.setCurrentLevel(stats.hitpoints, 200)

        player.statAdd(stats.hitpoints, constant = 0, percent = 100)
        assertEquals(255, player.hitpoints)

        player.statAdd(stats.hitpoints, constant = 100, percent = 0)
        assertEquals(255, player.hitpoints)

        player.statAdd(stats.hitpoints, constant = 0, percent = 100)
        assertEquals(255, player.hitpoints)

        player.statAdd(stats.hitpoints, constant = 100, percent = 100)
        assertEquals(255, player.hitpoints)
    }

    @Test
    fun GameTestState.`statBoost caps correctly based on constant and percent`() = runGameTest {
        player.setBaseLevel(stats.hitpoints, 99)
        player.setCurrentLevel(stats.hitpoints, 99)

        player.statBoost(stats.hitpoints, constant = 5, percent = 10)
        assertEquals(113, player.hitpoints)

        player.statBoost(stats.hitpoints, constant = 5, percent = 10)
        assertEquals(113, player.hitpoints)
    }

    @Test
    fun GameTestState.`statHeal uses base stat level for percent boost`() = runGameTest {
        player.setBaseLevel(stats.hitpoints, 99)
        player.setCurrentLevel(stats.hitpoints, 1)

        player.statHeal(stats.hitpoints, constant = 0, percent = 100)
        assertEquals(99, player.hitpoints)
    }
}
