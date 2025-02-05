package org.rsmod.game

import kotlin.math.min

public class MapClock(public var cycle: Int = 0) {
    public fun tick() {
        check(cycle < Int.MAX_VALUE)
        cycle++
    }

    public operator fun rem(cycles: Int): Int = cycle % cycles

    public operator fun plus(cycles: Int): Int =
        min(Int.MAX_VALUE.toLong(), cycle.toLong() + cycles).toInt()

    public operator fun minus(cycles: Int): Int = cycle - cycles

    public operator fun compareTo(value: Int): Int = cycle.compareTo(value)

    override fun toString(): String = "MapClock(cycle=$cycle)"
}
