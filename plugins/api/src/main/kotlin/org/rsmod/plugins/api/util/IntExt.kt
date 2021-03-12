package org.rsmod.plugins.api.util

private val BIT_SIZES = IntArray(Int.SIZE_BITS).apply {
    var size = 2
    for (i in indices) {
        this[i] = size - 1
        size += size
    }
}

/**
 * Returns the integer value represented by the bits within [lsb] to [msb] from the receiver
 * [Int] value.
 */
fun Int.extractBitValue(lsb: Int, msb: Int): Int {
    val size = BIT_SIZES[msb - lsb]
    return (this shr lsb) and size
}

/**
 * Returns the receiver [Int] value with bits from [lsb] to [msb] modified to represent [value].
 */
fun Int.withBitValue(lsb: Int, msb: Int, value: Int): Int {
    val ceil = BIT_SIZES[msb - lsb] shl lsb
    return (this and ceil.inv()) or ((value shl lsb) and ceil)
}
