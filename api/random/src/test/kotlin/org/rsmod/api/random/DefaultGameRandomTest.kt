package org.rsmod.api.random

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@Suppress("konsist.avoid usage of stdlib Random in functions")
class DefaultGameRandomTest {
    @Test
    fun `produce identical results with predetermined seed`() {
        val random = DefaultGameRandom(123)
        assertEquals(191, random.of(255))
        assertEquals(60, random.of(255))
        assertEquals(43, random.of(255))
        assertEquals(168, random.of(255))
        assertEquals(222, random.of(255))
    }

    @Test
    fun `return upper bound when range has no steps`() {
        val random = DefaultGameRandom(123)
        assertEquals(5, random.of(5..5))
    }

    @Test
    fun `return zero when maxExclusive is one`() {
        val random = DefaultGameRandom(123)
        assertEquals(0, random.of(1))
    }
}
