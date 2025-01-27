package org.rsmod.api.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.GameTestState
import org.rsmod.map.CoordGrid

class PlayerDelayTest {
    @Test
    fun GameTestState.`is not delayed on first map clock`() = runBasicGameTest {
        withPlayer {
            check(mapClock.cycle == 0)
            assertFalse(isDelayed)
            assertTrue(isNotDelayed)
        }
    }

    @Test
    fun GameTestState.`has not recently moved on first map clock`() = runBasicGameTest {
        withPlayer {
            check(mapClock.cycle == 0)
            check(routeDestination.isEmpty())
            assertFalse(hasMovedThisCycle)
        }
    }

    @Test
    fun GameTestState.`ensure player cannot cancel coroutine early`() = runGameTest {
        val start = CoordGrid(0, 50, 50, 0, 0)
        val dest = CoordGrid(0, 50, 50, 10, 10)
        val intercept = CoordGrid(0, 50, 50, 1, 1)

        // Ensure `moveGameClick` works as expected for test validity.
        player.teleport(start)
        player.moveGameClick(intercept)
        advance(ticks = 1)
        check(player.coords == intercept)

        player.teleport(start)
        player.withProtectedAccess {
            delay(1)
            teleport(dest)
        }
        check(player.isDelayed)
        check(player.coords == start)
        checkNotNull(player.activeCoroutine)

        player.moveGameClick(intercept)
        assertEquals(start, player.coords)
        assertNotNull(player.activeCoroutine)
        advance(ticks = 1)
        player.moveGameClick(intercept)
        assertTrue(player.isNotDelayed)
        assertEquals(dest, player.coords)
        assertNull(player.activeCoroutine)
    }
}
