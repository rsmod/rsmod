package org.rsmod.map.zone

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey.Companion.LEVEL_BIT_MASK
import org.rsmod.map.zone.ZoneKey.Companion.X_BIT_MASK
import org.rsmod.map.zone.ZoneKey.Companion.Z_BIT_MASK

class ZoneKeyTest {
    @Test
    fun `construct every zone key`() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val key = ZoneKey(x, z, level)
                    assertEquals(x, key.x)
                    assertEquals(z, key.z)
                    assertEquals(level, key.level)
                }
            }
        }
    }

    @Test
    fun `deconstruct every zone key`() {
        for (level in 0..LEVEL_BIT_MASK) {
            for (z in 0..Z_BIT_MASK) {
                for (x in 0..X_BIT_MASK) {
                    val key = ZoneKey(x, z, level)
                    val (c1, c2, c3) = key
                    assertEquals(x, c1)
                    assertEquals(z, c2)
                    assertEquals(level, c3)
                }
            }
        }
    }

    @Test
    fun `fail to construct on out of bounds zone key`() {
        assertThrows<IllegalArgumentException> { ZoneKey(X_BIT_MASK + 1, 0, 0) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, Z_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, 0, LEVEL_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { ZoneKey(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, -1, 0) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, 0, -1) }
        assertThrows<IllegalArgumentException> { ZoneKey(X_BIT_MASK, 0, 0).translateX(1) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, Z_BIT_MASK, 0).translateZ(1) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, 0, LEVEL_BIT_MASK).translateLevel(1) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, 0, 0).translateX(-1) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, 0, 0).translateZ(-1) }
        assertThrows<IllegalArgumentException> { ZoneKey(0, 0, 0).translateLevel(-1) }
    }

    @Test
    fun `convert from coordinates`() {
        val coords = CoordGrid(3200, 4800, 1)
        val key = ZoneKey.from(coords)
        assertEquals(400, key.x)
        assertEquals(600, key.z)
        assertEquals(1, key.level)
    }

    @Test
    fun `convert to coordinates`() {
        val key = ZoneKey(x = 400, z = 600, level = 1)
        val coords = key.toCoords()
        assertEquals(3200, coords.x)
        assertEquals(4800, coords.z)
        assertEquals(1, coords.level)
    }
}
