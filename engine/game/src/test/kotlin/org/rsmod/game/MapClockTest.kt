package org.rsmod.game

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MapClockTest {
    @Test
    fun `greater than numerical value`() {
        val clock = MapClock(15)
        val value = 10
        assertTrue(clock > value)
    }

    @Test
    fun `greater than or equal to numerical value`() {
        val clock = MapClock(10)
        val value = 10
        assertTrue(clock >= value)
        assertFalse(clock > value)
    }

    @Test
    fun `less than numerical value`() {
        val clock = MapClock(10)
        val value = 15
        assertTrue(clock < value)
    }

    @Test
    fun `less than or equal to numerical value`() {
        val clock = MapClock(15)
        val value = 15
        assertTrue(clock <= value)
        assertFalse(clock < value)
    }
}
