package org.rsmod.utils.bits

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

class BitsTest {
    @Test
    fun `getBits from zeroed bits should return zero`() {
        val value = 0
        assertEquals(0, value.getBits(0..3))
        assertEquals(0, value.getBits(4..7))
        assertEquals(0, value.getBits(8..15))
        assertEquals(0, value.getBits(16..31))
    }

    @Test
    fun `set and get individual bits from full integer`() {
        for (bit in 0 until Int.SIZE_BITS) {
            val value = 1 shl bit
            assertEquals(1, value.withBits(bit..bit, 1).getBits(bit..bit))
            assertEquals(0, value.withBits(bit..bit, 0).getBits(bit..bit))
        }
    }

    @Test
    fun `set and get bits with overlapping ranges`() {
        var value = 0

        // Set first 4 bits to 1111.
        @Suppress("KotlinConstantConditions")
        value = value.withBits(0..3, 0b1111)
        assertEquals(0b1111, value.getBits(0..3))
        assertEquals(0b0000, value.getBits(4..7))

        // Set next 4 bits to 1111.
        value = value.withBits(4..7, 0b1111)
        assertEquals(0b1111, value.getBits(4..7))
        assertEquals(0b1111, value.getBits(0..3))

        // All 8 bits we worked with should be set to 1.
        assertEquals(0b11111111, value.getBits(0..7))
    }

    @Test
    fun `set and get upper bound bits`() {
        val bits = 30..31
        val value = 0.withBits(bits, 0b11)
        assertEquals(0b11, value.getBits(bits))
    }

    @Test
    fun `set and get 32-bit bounds`() {
        val bits = 0..31
        // 32-bit ranges allows for negatives.
        assertEquals(-Int.MAX_VALUE, 0.withBits(bits, -Int.MAX_VALUE).getBits(bits))
        assertEquals(0, 0.withBits(bits, 0).getBits(bits))
        assertEquals(Int.MAX_VALUE, 0.withBits(bits, Int.MAX_VALUE).getBits(bits))
    }

    @ParameterizedTest
    @ArgumentsSource(BitMaskProvider::class)
    fun `set and get bounds`(bits: IntRange, maxValue: Int) {
        // When set to -1, `withBits` will set the value in bit range to max value.
        assertEquals(maxValue, 0.withBits(bits, -1).getBits(bits))
        assertEquals(0, 0.withBits(bits, 0).getBits(bits))
        assertEquals(maxValue, 0.withBits(bits, maxValue).getBits(bits))
    }
}
