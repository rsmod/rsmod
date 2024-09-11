package org.rsmod.map.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TranslationTest {
    @Test
    fun `constructor correctly packs values`() {
        val translation = Translation(x = 1000, z = -500, level = 2)
        assertEquals(1000, translation.x)
        assertEquals(-500, translation.z)
        assertEquals(2, translation.level)
    }

    @Test
    fun `constructor handles maximum and minimum values`() {
        val max = Translation(x = 8191, z = 8191, level = 3)
        assertEquals(8191, max.x)
        assertEquals(8191, max.z)
        assertEquals(3, max.level)

        val min = Translation(x = -8192, z = -8192, level = -4)
        assertEquals(-8192, min.x)
        assertEquals(-8192, min.z)
        assertEquals(-4, min.level)
    }

    @Test
    fun `copy method works correctly`() {
        val translation = Translation(x = 100, z = 200, level = 1)
        val copy = translation.copy(x = 500)
        assertEquals(500, copy.x)
        assertEquals(200, copy.z)
        assertEquals(1, copy.level)
    }

    @Test
    fun `absolute method returns positive values`() {
        val translation = Translation(x = -500, z = -300, level = -2)
        val absolute = translation.absolute()
        assertEquals(500, absolute.x)
        assertEquals(300, absolute.z)
        assertEquals(2, absolute.level)
    }

    @Test
    fun `plus operator adds translations correctly`() {
        val t1 = Translation(x = 100, z = 200, level = 1)
        val t2 = Translation(x = 50, z = -100, level = 0)
        val result = t1 + t2
        assertEquals(150, result.x)
        assertEquals(100, result.z)
        assertEquals(1, result.level)
    }

    @Test
    fun `minus operator subtracts translations correctly`() {
        val t1 = Translation(x = 100, z = 200, level = 1)
        val t2 = Translation(x = 50, z = 100, level = 0)
        val result = t1 - t2
        assertEquals(50, result.x)
        assertEquals(100, result.z)
        assertEquals(1, result.level)
    }

    @Test
    fun `times operator multiplies translations correctly`() {
        val a = Translation(x = 2, z = 3, level = 1)
        val b = Translation(x = 5, z = 2, level = 3)
        val result = a * b
        assertEquals(10, result.x)
        assertEquals(6, result.z)
        assertEquals(3, result.level)
    }

    @Test
    fun `fail to construct on out of bound translation`() {
        assertThrows<IllegalArgumentException> { Translation(x = 8192, z = 0, level = 0) }
        assertThrows<IllegalArgumentException> { Translation(x = -8193, z = 0, level = 0) }
        assertThrows<IllegalArgumentException> { Translation(x = 0, z = 8192, level = 0) }
        assertThrows<IllegalArgumentException> { Translation(x = 0, z = -8193, level = 0) }
        assertThrows<IllegalArgumentException> { Translation(x = 0, z = 0, level = 4) }
        assertThrows<IllegalArgumentException> { Translation(x = 0, z = 0, level = -5) }
    }
}
