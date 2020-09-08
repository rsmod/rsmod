package gg.rsmod.cache.util

/**
 * A utility class containing bit-related data for calculations and
 * manipulation.
 */
object BitConstants {

    val BIT_MASK = IntArray(Int.SIZE_BITS).apply {
        for (i in indices) {
            this[i] = (1 shl i) - 1
        }
    }

    val BIT_SIZES = IntArray(Int.SIZE_BITS).apply {
        var currValue = 2
        for (i in indices) {
            this[i] = currValue - 1
            currValue += currValue
        }
    }
}
