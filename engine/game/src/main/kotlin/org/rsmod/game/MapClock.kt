package org.rsmod.game

import kotlin.math.min

public class MapClock(public var cycle: Int = 0) {
    public fun tick() {
        cycle++
    }

    public operator fun plus(cycles: Int): Int =
        min(Int.MAX_VALUE.toLong(), cycle.toLong() + cycles).toInt()

    public operator fun compareTo(value: Int): Int = cycle.compareTo(value)
}
