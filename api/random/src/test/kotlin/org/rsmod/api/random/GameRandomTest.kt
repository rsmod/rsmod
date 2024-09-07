package org.rsmod.api.random

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.api.testing.random.FixedRandom
import org.rsmod.api.testing.random.SequenceRandom
import org.rsmod.map.CoordGrid

@Suppress("konsist.avoid usage of stdlib Random in functions")
class GameRandomTest {
    @Test
    fun `random number within max exclusive`() {
        val random = FixedRandom(1)
        assertEquals(1, random.of(3))

        random.set(2)
        assertEquals(2, random.of(3))
    }

    @Test
    fun `random number with maxExclusive equal to 1`() {
        val random = FixedRandom(0)
        assertEquals(0, random.of(1))
    }

    @Test
    fun `random number within min and max inclusive`() {
        val random = FixedRandom(3)
        assertEquals(3, random.of(1, 5))

        random.set(5)
        assertEquals(5, random.of(1, 5))

        random.set(0)
        assertThrows<IllegalStateException> { random.of(1, 5) }
    }

    @Test
    fun `random number within inclusive range`() {
        val random = FixedRandom(3)
        assertEquals(3, random.of(1..5))

        random.set(5)
        assertEquals(5, random.of(1..5))
    }

    @Test
    fun `random boolean with maxExclusive`() {
        val random = FixedRandom(0)
        assertTrue(random.randomBoolean(1))

        random.set(1)
        assertFalse(random.randomBoolean(2))

        random.set(0)
        assertTrue(random.randomBoolean(2))
    }

    @Test
    fun `random CoordGrid translation with radius`() {
        val random = SequenceRandom()
        val baseCoord = CoordGrid(5, 5)
        val radius = 3

        random.next = 0
        random.then = 0
        assertEquals(baseCoord, random.of(baseCoord, radius))

        random.next = 2
        random.then = 1
        assertEquals(CoordGrid(7, 6), random.of(baseCoord, radius))

        random.next = 2
        random.then = -1
        assertEquals(CoordGrid(7, 4), random.of(baseCoord, radius))

        random.next = -3
        random.then = -2
        assertEquals(CoordGrid(2, 3), random.of(baseCoord, radius))

        random.next = -3
        random.then = 2
        assertEquals(CoordGrid(2, 7), random.of(baseCoord, radius))
    }

    @Test
    fun `pick two elements`() {
        val random = FixedRandom()

        random.set(0)
        assertEquals("first", random.pick("first", "second"))

        random.set(1)
        assertEquals("second", random.pick("first", "second"))

        random.set(2)
        assertThrows<IllegalStateException> { random.pick("first", "second") }
    }

    @Test
    fun `pick three elements`() {
        val random = FixedRandom()

        random.set(0)
        assertEquals("first", random.pick("first", "second"))

        random.set(1)
        assertEquals("second", random.pick("first", "second"))

        random.set(2)
        assertEquals("third", random.pick("first", "second", "third"))

        random.set(3)
        assertThrows<IllegalStateException> { random.pick("first", "second", "third") }
    }

    @Test
    fun `pick four elements`() {
        val random = FixedRandom()

        random.set(0)
        assertEquals("first", random.pick("first", "second"))

        random.set(1)
        assertEquals("second", random.pick("first", "second"))

        random.set(2)
        assertEquals("third", random.pick("first", "second", "third"))

        random.set(3)
        assertEquals("fourth", random.pick("first", "second", "third", "fourth"))

        random.set(4)
        assertThrows<IllegalStateException> { random.pick("first", "second", "third", "fourth") }
    }

    @Test
    fun `pick five elements`() {
        val random = FixedRandom()

        random.set(0)
        assertEquals("first", random.pick("first", "second"))

        random.set(1)
        assertEquals("second", random.pick("first", "second"))

        random.set(2)
        assertEquals("third", random.pick("first", "second", "third"))

        random.set(3)
        assertEquals("fourth", random.pick("first", "second", "third", "fourth"))

        random.set(4)
        assertEquals("fifth", random.pick("first", "second", "third", "fourth", "fifth"))

        random.set(5)
        assertThrows<IllegalStateException> {
            random.pick("first", "second", "third", "fourth", "fifth")
        }
    }

    @Test
    fun `pickOrNull from array`() {
        val random = FixedRandom(0)

        val emptyArray = emptyArray<String>()
        assertNull(random.pickOrNull(emptyArray))

        val array = arrayOf("first", "second")
        random.set(1)
        assertEquals("second", random.pickOrNull(array))
    }

    @Test
    fun `pick from large array`() {
        val random = FixedRandom(999)
        val largeArray = Array(1000) { "element$it" }
        assertEquals("element999", random.pickOrNull(largeArray))
    }

    @Test
    fun `pickOrNull from collection`() {
        val random = FixedRandom(0)

        val emptyList = emptyList<String>()
        assertNull(random.pickOrNull(emptyList))

        val list = listOf("first", "second")
        random.set(1)
        assertEquals("second", random.pickOrNull(list))
    }
}
