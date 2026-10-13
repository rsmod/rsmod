package org.runerealm.content.skills.agility

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.testing.GameTestState
import org.runerealm.game.map.collision.isWalkBlocked

class ShortcutDataTest {
    @Test
    fun GameTestState.`every shortcut loc exists in the cache`() {
        val names = cacheTypes.locs.values.map { it.internalName }.toSet()
        for (shortcut in SHORTCUTS) {
            for (loc in shortcut.locs) {
                assertTrue(loc in names, "${shortcut.name}: unknown loc `$loc`")
            }
        }
    }

    @Test
    fun GameTestState.`every shortcut ends on walkable tiles`() {
        for (shortcut in SHORTCUTS) {
            assertFalse(
                collision.isWalkBlocked(shortcut.sideA),
                "${shortcut.name}: ${shortcut.sideA}",
            )
            assertFalse(
                collision.isWalkBlocked(shortcut.sideB),
                "${shortcut.name}: ${shortcut.sideB}",
            )
        }
    }

    @Test
    fun GameTestState.`shortcut names are unique`() {
        assertEquals(SHORTCUTS.size, SHORTCUTS.map { it.name }.toSet().size)
    }
}
