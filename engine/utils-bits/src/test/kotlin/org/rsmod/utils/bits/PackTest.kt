package org.rsmod.utils.bits

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

class PackTest {
    @Test
    fun `pack zero bits`() {
        assertEquals(0, Bits.pack(0, 0..0, 0))
    }

    @Test
    fun `pack empty bit range`() {
        assertEquals(0, Bits.pack(0, 5..4, 0))
    }

    @Test
    fun `pack overlapping bit ranges`() {
        var result = 0

        // Packed bits come out to 00000111.
        result = Bits.pack(result, 0..2, 7)
        check(result == 7)

        // Packed bits come out to 00011111. (00000111 | 00011111)
        result = Bits.pack(result, 2..4, 7)
        assertEquals(31, result)
    }

    @Test
    fun `pack single bit in various positions`() {
        val positions = listOf(0, 1, 15, 31)
        for (pos in positions) {
            assertEquals(1 shl pos, Bits.pack(0, pos..pos, 1))
            assertEquals(0, Bits.pack(1 shl pos, pos..pos, 0))
        }
    }

    @Test
    fun `pack invalid bit range`() {
        assertThrows<BitRangeOutOfBounds> { Bits.pack(0, -1..5, 0) }
        assertThrows<BitRangeOutOfBounds> { Bits.pack(0, 0..32, 0) }
        assertThrows<BitRangeOutOfBounds> { Bits.pack(0, -1..32, 0) }
    }

    @Test
    fun `pack bits on 32-bit range`() {
        val bits = 0..31
        val whole = 0
        assertEquals(-Int.MAX_VALUE, Bits.pack(whole, bits, -Int.MAX_VALUE))
        assertEquals(0, Bits.pack(whole, bits, 0))
        assertEquals(Int.MAX_VALUE, Bits.pack(whole, bits, Int.MAX_VALUE))
    }

    @Test
    fun `check bounds on 32-bit pack`() {
        assertDoesNotThrow { Bits.pack(0, 0 until 32, 0) }
        assertDoesNotThrow { Bits.pack(0, 0 until 32, Int.MAX_VALUE) }
        assertDoesNotThrow { Bits.pack(0, 0 until 32, -Int.MAX_VALUE) }
    }

    @ParameterizedTest
    @ArgumentsSource(BitMaskProvider::class)
    fun `check bounds on pack`(bits: IntRange, maxValue: Int) {
        assertDoesNotThrow { Bits.pack(0, bits, -maxValue) }
        assertDoesNotThrow { Bits.pack(0, bits, 0) }
        assertDoesNotThrow { Bits.pack(0, bits, maxValue) }
        assertThrows<BitValueOutOfBounds> { Bits.pack(0, bits, -(maxValue + 1)) }
        assertThrows<BitValueOutOfBounds> { Bits.pack(0, bits, maxValue + 1) }
    }
}
