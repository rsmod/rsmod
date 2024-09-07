package org.rsmod.map.square

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.map.square.MapSquareGrid.Companion.LEVEL_BIT_MASK
import org.rsmod.map.square.MapSquareGrid.Companion.X_BIT_MASK
import org.rsmod.map.square.MapSquareGrid.Companion.Z_BIT_MASK

class MapSquareGridTest {
    @Test
    fun `construct every map square grid`() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val coords = MapSquareGrid(x, z, level)
                    assertEquals(x, coords.x)
                    assertEquals(z, coords.z)
                    assertEquals(level, coords.level)
                }
            }
        }
    }

    @Test
    fun `deconstruct every map square grid`() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val coords = MapSquareGrid(x, z, level)
                    val (c1, c2, c3) = coords
                    assertEquals(x, c1)
                    assertEquals(z, c2)
                    assertEquals(level, c3)
                }
            }
        }
    }

    @Test
    fun `fail to construct on out of bound map square grid`() {
        assertThrows<IllegalArgumentException> { MapSquareGrid(X_BIT_MASK + 1, 0, 0) }
        assertThrows<IllegalArgumentException> { MapSquareGrid(0, Z_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { MapSquareGrid(0, 0, LEVEL_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { MapSquareGrid(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { MapSquareGrid(0, -1, 0) }
        assertThrows<IllegalArgumentException> { MapSquareGrid(0, 0, -1) }
        assertThrows<IllegalArgumentException> { MapSquareGrid(X_BIT_MASK, 0, 0).translateX(1) }
        assertThrows<IllegalArgumentException> { MapSquareGrid(0, Z_BIT_MASK, 0).translateZ(1) }
        assertThrows<IllegalArgumentException> {
            MapSquareGrid(0, 0, LEVEL_BIT_MASK).translateLevel(1)
        }
        assertThrows<IllegalArgumentException> { MapSquareGrid(0, 0, 0).translateX(-1) }
        assertThrows<IllegalArgumentException> { MapSquareGrid(0, 0, 0).translateZ(-1) }
        assertThrows<IllegalArgumentException> { MapSquareGrid(0, 0, 0).translateLevel(-1) }
    }

    @Test
    fun `add map square grids`() {
        val lhs = MapSquareGrid(30, 30, 1)
        val rhs = MapSquareGrid(25, 0, 1)
        val sum = lhs + rhs
        assertEquals(55, sum.x)
        assertEquals(30, sum.z)
        assertEquals(2, sum.level)
    }

    @Test
    fun `subtract map square grids`() {
        val lhs = MapSquareGrid(30, 30, 1)
        val rhs = MapSquareGrid(25, 0, 1)
        val diff = lhs - rhs
        assertEquals(5, diff.x)
        assertEquals(30, diff.z)
        assertEquals(0, diff.level)
    }

    @Test
    fun `correctly convert coordinate into map square grid`() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val convert =
                        MapSquareGrid.fromAbsolute(
                            x = X_BIT_MASK + 1 + x,
                            z = Z_BIT_MASK + 1 + z,
                            level = LEVEL_BIT_MASK + 1 + level,
                        )
                    assertEquals(x, convert.x)
                    assertEquals(z, convert.z)
                    assertEquals(level, convert.level)
                }
            }
        }
    }
}
