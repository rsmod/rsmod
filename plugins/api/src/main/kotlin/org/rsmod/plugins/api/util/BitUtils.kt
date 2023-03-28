package org.rsmod.plugins.api.util

public object BitUtils {

    public fun modify(value: Int, bitRange: IntRange, rangeValue: Int): Int {
        val len = bitRange.last - bitRange.first
        val mask = i32BitSizes[len] shl bitRange.first
        return (value and mask.inv()) or ((rangeValue shl bitRange.first) and mask)
    }

    public fun get(value: Int, bitRange: IntRange): Int {
        val len = bitRange.last - bitRange.first
        return (value shr bitRange.first) and i32BitSizes[len]
    }

    private val i32BitSizes = IntArray(Int.SIZE_BITS).apply {
        var size = 2
        for (i in indices) {
            set(i, size - 1)
            size += size
        }
    }
}
