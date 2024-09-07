package org.rsmod.utils.bits

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

class UnpackTest {
    @Test
    fun `unpack zero bits`() {
        assertEquals(0, Bits.unpack(0, 0..0))
    }

    @Test
    fun `unpack zeroed bits`() {
        assertEquals(0, Bits.unpack(0, 0..3))
    }

    @Test
    fun `unpack empty bit range`() {
        assertEquals(0, Bits.unpack(0, 5..4))
    }

    @Test
    fun `unpack specific bit patterns`() {
        val whole = 170
        assertEquals(1, Bits.unpack(whole, 1..2))
        assertEquals(2, Bits.unpack(whole, 4..6))
    }

    @Test
    fun `unpack invalid bit range`() {
        assertThrows<BitRangeOutOfBounds> { Bits.unpack(0, -1..5) }
        assertThrows<BitRangeOutOfBounds> { Bits.unpack(0, 0..32) }
        assertThrows<BitRangeOutOfBounds> { Bits.unpack(0, -1..32) }
    }

    @Test
    fun `unpack bits on 32-bit range`() {
        val bits = 0..31
        assertEquals(-Int.MAX_VALUE, Bits.unpack(-Int.MAX_VALUE, bits))
        assertEquals(0, Bits.unpack(0, bits))
        assertEquals(Int.MAX_VALUE, Bits.unpack(Int.MAX_VALUE, bits))
    }

    @ParameterizedTest
    @ArgumentsSource(BitMaskProvider::class)
    fun `unpack bits`(bits: IntRange, maxValue: Int) {
        val whole = 0.withBits(bits, maxValue)
        assertEquals(maxValue, Bits.unpack(whole, bits))
    }
}
