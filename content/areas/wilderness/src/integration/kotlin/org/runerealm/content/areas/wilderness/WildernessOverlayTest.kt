package org.runerealm.content.areas.wilderness

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.area.checker.WildernessArea
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.testing.GameTestState
import org.runerealm.map.CoordGrid

class WildernessOverlayTest {
    @Test
    fun GameTestState.`wilderness levels match the client`() {
        assertEquals(0, WildernessArea.level(CoordGrid(3222, 3218, 0)))
        // The client proc counts the ditch strip (z 3520-3524) as level 1...
        assertEquals(1, WildernessArea.clientLevel(CoordGrid(3100, 3520, 0)))
        assertEquals(1, WildernessArea.clientLevel(CoordGrid(3100, 3524, 0)))
        // ...but the tile south of the ditch, the ditch and the two tiles north of it are safe.
        for (z in 3520..3524) {
            assertEquals(0, WildernessArea.level(CoordGrid(3100, z, 0)), "z=$z")
            assertFalse(WildernessArea.contains(CoordGrid(3100, z, 0)), "z=$z")
        }
        assertEquals(1, WildernessArea.level(CoordGrid(3100, 3525, 0)))
        assertTrue(WildernessArea.contains(CoordGrid(3100, 3525, 0)))
        assertEquals(1, WildernessArea.level(CoordGrid(3100, 3527, 0)))
        assertEquals(2, WildernessArea.level(CoordGrid(3100, 3528, 0)))
        assertEquals(56, WildernessArea.level(CoordGrid(3100, 3960, 0)))
        assertFalse(WildernessArea.contains(CoordGrid(3137, 3630, 0)))
        assertTrue(WildernessArea.contains(CoordGrid(3100, 3600, 0)))
    }

    @Test
    fun GameTestState.`overlay only shows inside the wilderness`() =
        runGameTest(WildernessOverlay::class) {
            player.softTimer(wilderness_timers.check, 1)
            val outside = CoordGrid(3222, 3218, 0)
            val inside = CoordGrid(3100, 3600, 0)

            player.teleport(outside)
            advance(ticks = 2)
            assertFalse(player.ui.containsOverlay(interfaces.pvp_icons))

            player.teleport(inside)
            advance(ticks = 2)
            assertTrue(player.ui.containsOverlay(interfaces.pvp_icons))

            player.teleport(outside)
            advance(ticks = 2)
            assertFalse(player.ui.containsOverlay(interfaces.pvp_icons))
        }

    @Test
    fun GameTestState.`the two tiles north of the ditch show no overlay`() =
        runGameTest(WildernessOverlay::class) {
            player.softTimer(wilderness_timers.check, 1)
            player.teleport(CoordGrid(3100, 3523, 0))
            advance(ticks = 2)
            assertFalse(player.ui.containsOverlay(interfaces.pvp_icons))

            player.teleport(CoordGrid(3100, 3524, 0))
            advance(ticks = 2)
            assertFalse(player.ui.containsOverlay(interfaces.pvp_icons))

            player.teleport(CoordGrid(3100, 3525, 0))
            advance(ticks = 2)
            assertTrue(player.ui.containsOverlay(interfaces.pvp_icons))
        }
}
