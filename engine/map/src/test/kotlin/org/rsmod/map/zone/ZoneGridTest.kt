package org.rsmod.map.zone

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.map.zone.ZoneGrid.Companion.LEVEL_BIT_MASK
import org.rsmod.map.zone.ZoneGrid.Companion.X_BIT_MASK
import org.rsmod.map.zone.ZoneGrid.Companion.Z_BIT_MASK

class ZoneGridTest {
    @Test
    fun `construct every zone grid`() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val coords = ZoneGrid(x, z, level)
                    assertEquals(x, coords.x)
                    assertEquals(z, coords.z)
                    assertEquals(level, coords.level)
                }
            }
        }
    }

    @Test
    fun `deconstruct every zone grid`() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val coords = ZoneGrid(x, z, level)
                    val (c1, c2, c3) = coords
                    assertEquals(x, c1)
                    assertEquals(z, c2)
                    assertEquals(level, c3)
                }
            }
        }
    }

    @Test
    fun `fail to construct on out of bound zone grid`() {
        assertThrows<IllegalArgumentException> { ZoneGrid(X_BIT_MASK + 1, 0, 0) }
        assertThrows<IllegalArgumentException> { ZoneGrid(0, Z_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { ZoneGrid(0, 0, LEVEL_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { ZoneGrid(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { ZoneGrid(0, -1, 0) }
        assertThrows<IllegalArgumentException> { ZoneGrid(0, 0, -1) }
        assertThrows<IllegalArgumentException> { ZoneGrid(X_BIT_MASK, 0, 0).translateX(1) }
        assertThrows<IllegalArgumentException> { ZoneGrid(0, Z_BIT_MASK, 0).translateZ(1) }
        assertThrows<IllegalArgumentException> { ZoneGrid(0, 0, LEVEL_BIT_MASK).translateLevel(1) }
        assertThrows<IllegalArgumentException> { ZoneGrid(0, 0, 0).translateX(-1) }
        assertThrows<IllegalArgumentException> { ZoneGrid(0, 0, 0).translateZ(-1) }
        assertThrows<IllegalArgumentException> { ZoneGrid(0, 0, 0).translateLevel(-1) }
    }

    @Test
    fun `add zone grids`() {
        val lhs = ZoneGrid(3, 7, 1)
        val rhs = ZoneGrid(3, 0, 1)
        val sum = lhs + rhs
        assertEquals(6, sum.x)
        assertEquals(7, sum.z)
        assertEquals(2, sum.level)
    }

    @Test
    fun `subtract zone grids`() {
        val lhs = ZoneGrid(3, 7, 1)
        val rhs = ZoneGrid(3, 0, 1)
        val diff = lhs - rhs
        assertEquals(0, diff.x)
        assertEquals(7, diff.z)
        assertEquals(0, diff.level)
    }

    @Test
    fun `correctly convert coordinates into zone grid`() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val convert =
                        ZoneGrid.fromAbsolute(
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
