package org.rsmod.utils.bits

public val Int.bitMask: Int
    get() = ((1L shl this) - 1).toInt()

// Use Long to fit 32-bit bitmask.
public val IntRange.bitMask: Long
    get() {
        val bitCount = last - first + 1
        val bitMask = (1L shl bitCount) - 1
        return bitMask
    }

public fun Int.withBits(bits: IntRange, bitValue: Int): Int {
    val len = bits.last - bits.first + 1
    val mask = len.bitMask shl bits.first
    return (this and mask.inv()) or ((bitValue shl bits.first) and mask)
}

public fun Int.getBits(bits: IntRange): Int {
    val len = bits.last - bits.first + 1
    return (this shr bits.first) and len.bitMask
}

public object Bits {
    public fun unpack(whole: Int, bits: IntRange): Int {
        if (bits.first < 0 || bits.last >= Int.SIZE_BITS) {
            throw BitRangeOutOfBounds("`bits` range ($bits) out of valid 32-bit bounds.")
        }
        return whole.getBits(bits)
    }

    public fun pack(whole: Int, bits: IntRange, bitValue: Int): Int {
        if (bits.first < 0 || bits.last >= Int.SIZE_BITS) {
            throw BitRangeOutOfBounds("`bits` range ($bits) out of valid 32-bit bounds.")
        }
        val validRange = -bits.bitMask..bits.bitMask
        if (bitValue !in validRange) {
            val bitCount = bits.last - bits.first + 1
            throw BitValueOutOfBounds(
                "`bitValue` ($bitValue) exceeds the valid value ranges [$validRange] " +
                    "that can be represented by the given bit range " +
                    "($bitCount bit${if (bitCount != 1) "s" else ""}: $bits)."
            )
        }
        return whole.withBits(bits, bitValue)
    }
}

public class BitRangeOutOfBounds(message: String? = null) : IndexOutOfBoundsException(message)

public class BitValueOutOfBounds(message: String? = null) : IndexOutOfBoundsException(message)
