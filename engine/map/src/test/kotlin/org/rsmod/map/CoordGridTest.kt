package org.rsmod.map

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.map.CoordGrid.Companion.LEVEL_BIT_MASK
import org.rsmod.map.CoordGrid.Companion.X_BIT_MASK
import org.rsmod.map.CoordGrid.Companion.Z_BIT_MASK

class CoordGridTest {
    @Test
    fun `construct every coordinate`() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val coords = CoordGrid(x, z, level)
                    assertEquals(x, coords.x)
                    assertEquals(z, coords.z)
                    assertEquals(level, coords.level)
                }
            }
        }
    }

    @Test
    fun `deconstruct every coordinate`() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val coords = CoordGrid(x, z, level)
                    val (c1, c2, c3) = coords
                    assertEquals(x, c1)
                    assertEquals(z, c2)
                    assertEquals(level, c3)
                }
            }
        }
    }

    @Test
    fun `fail to construct on out of bound coordinate`() {
        assertThrows<IllegalArgumentException> { CoordGrid(X_BIT_MASK + 1, 0, 0) }
        assertThrows<IllegalArgumentException> { CoordGrid(0, Z_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { CoordGrid(0, 0, LEVEL_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { CoordGrid(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { CoordGrid(0, -1, 0) }
        assertThrows<IllegalArgumentException> { CoordGrid(0, 0, -1) }
        assertThrows<IllegalArgumentException> { CoordGrid(X_BIT_MASK, 0, 0).translateX(1) }
        assertThrows<IllegalArgumentException> { CoordGrid(0, Z_BIT_MASK, 0).translateZ(1) }
        assertThrows<IllegalArgumentException> { CoordGrid(0, 0, LEVEL_BIT_MASK).translateLevel(1) }
        assertThrows<IllegalArgumentException> { CoordGrid(0, 0, 0).translateX(-1) }
        assertThrows<IllegalArgumentException> { CoordGrid(0, 0, 0).translateZ(-1) }
        assertThrows<IllegalArgumentException> { CoordGrid(0, 0, 0).translateLevel(-1) }
    }

    @Test
    fun `add coordinates`() {
        val lhs = CoordGrid(3200, 3200, 1)
        val rhs = CoordGrid(2400, 1600, 1)
        val sum = lhs + rhs
        assertEquals(5600, sum.x)
        assertEquals(4800, sum.z)
        assertEquals(2, sum.level)
    }

    @Test
    fun `subtract coordinates`() {
        val lhs = CoordGrid(3200, 3200, 1)
        val rhs = CoordGrid(2400, 1600, 1)
        val diff = lhs - rhs
        assertEquals(800, diff.x)
        assertEquals(1600, diff.z)
        assertEquals(0, diff.level)
    }

    @Test
    fun `ensure NULL constant cannot be constructed`() {
        val upperBounds = CoordGrid(X_BIT_MASK, Z_BIT_MASK, LEVEL_BIT_MASK)
        assertNotEquals(CoordGrid.NULL, upperBounds)
    }

    @Test
    fun `guarantee the integrity of values from the ZERO constant`() {
        val zero = CoordGrid.ZERO
        assertEquals(0, zero.x)
        assertEquals(0, zero.z)
        assertEquals(0, zero.level)
    }
}
