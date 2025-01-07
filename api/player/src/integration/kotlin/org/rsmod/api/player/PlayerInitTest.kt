package org.rsmod.api.player

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.GameTestState

class PlayerInitTest {
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
}
