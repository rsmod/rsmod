package org.rsmod.game.map.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.map.util.I8Coordinates.Companion.LEVEL_BIT_MASK
import org.rsmod.game.map.util.I8Coordinates.Companion.X_BIT_MASK
import org.rsmod.game.map.util.I8Coordinates.Companion.Z_BIT_MASK

class I8CoordinatesTest {

    @Test
    fun testConstruct() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val coords = I8Coordinates(x, z, level)
                    assertEquals(x, coords.x)
                    assertEquals(z, coords.z)
                    assertEquals(level, coords.level)
                }
            }
        }
    }

    @Test
    fun testDeconstruct() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val coords = I8Coordinates(x, z, level)
                    val (c1, c2, c3) = coords
                    assertEquals(x, c1)
                    assertEquals(z, c2)
                    assertEquals(level, c3)
                }
            }
        }
    }

    @Test
    fun testConstructOutOfBounds() {
        assertThrows<IllegalArgumentException> { I8Coordinates(X_BIT_MASK + 1, 0, 0) }
        assertThrows<IllegalArgumentException> { I8Coordinates(0, Z_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { I8Coordinates(0, 0, LEVEL_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { I8Coordinates(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { I8Coordinates(0, -1, 0) }
        assertThrows<IllegalArgumentException> { I8Coordinates(0, 0, -1) }
    }

    @Test
    fun testTranslateOutOfBounds() {
        assertThrows<IllegalArgumentException> { I8Coordinates(X_BIT_MASK, 0, 0).translateX(1) }
        assertThrows<IllegalArgumentException> { I8Coordinates(0, Z_BIT_MASK, 0).translateZ(1) }
        assertThrows<IllegalArgumentException> { I8Coordinates(0, 0, LEVEL_BIT_MASK).translateLevel(1) }
        assertThrows<IllegalArgumentException> { I8Coordinates(0, 0, 0).translateX(-1) }
        assertThrows<IllegalArgumentException> { I8Coordinates(0, 0, 0).translateZ(-1) }
        assertThrows<IllegalArgumentException> { I8Coordinates(0, 0, 0).translateLevel(-1) }
    }

    @Test
    fun testPlusOperator() {
        val lhs = I8Coordinates(3, 7, 1)
        val rhs = I8Coordinates(3, 0, 1)
        val sum = lhs + rhs
        assertEquals(6, sum.x)
        assertEquals(7, sum.z)
        assertEquals(2, sum.level)
    }

    @Test
    fun testMinusOperator() {
        val lhs = I8Coordinates(3, 7, 1)
        val rhs = I8Coordinates(3, 0, 1)
        val diff = lhs - rhs
        assertEquals(0, diff.x)
        assertEquals(7, diff.z)
        assertEquals(0, diff.level)
    }

    @Test
    fun testAbsoluteCoordsConversion() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val convert = I8Coordinates.convert(
                        X_BIT_MASK + 1 + x,
                        Z_BIT_MASK + 1 + z,
                        LEVEL_BIT_MASK + 1 + level
                    )
                    assertEquals(x, convert.x)
                    assertEquals(z, convert.z)
                    assertEquals(level, convert.level)
                }
            }
        }
    }
}
