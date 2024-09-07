package org.rsmod.game

import kotlin.math.min

public class MapClock(public var tick: Int = 0) {
    public fun tick() {
        tick++
    }

    public operator fun plus(ticks: Int): Int =
        min(Int.MAX_VALUE.toLong(), tick.toLong() + ticks).toInt()

    public operator fun compareTo(value: Int): Int = tick.compareTo(value)
}
