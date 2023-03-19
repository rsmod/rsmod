package org.rsmod.game.map

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.map.Coordinates.Companion.LEVEL_BIT_MASK
import org.rsmod.game.map.Coordinates.Companion.X_BIT_MASK
import org.rsmod.game.map.Coordinates.Companion.Z_BIT_MASK

class CoordinatesTest {

    @Test
    fun testConstruct() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val coords = Coordinates(x, z, level)
                    assertEquals(x, coords.x)
                    assertEquals(z, coords.z)
                    assertEquals(level, coords.level)
                }
            }
        }
    }

    @Test
    fun testConstructFail() {
        assertThrows<IllegalArgumentException> { Coordinates(X_BIT_MASK + 1, 0, 0) }
        assertThrows<IllegalArgumentException> { Coordinates(0, Z_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { Coordinates(0, 0, LEVEL_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { Coordinates(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { Coordinates(0, -1, 0) }
        assertThrows<IllegalArgumentException> { Coordinates(0, 0, -1) }
    }

    @Test
    fun testNullConstantUnreachable() {
        val upperBounds = Coordinates(X_BIT_MASK, Z_BIT_MASK, LEVEL_BIT_MASK)
        assertNotEquals(Coordinates.NULL, upperBounds)
    }
}
