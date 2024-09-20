package org.rsmod.game.timer

import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap
import org.rsmod.game.type.timer.TimerType

public class PlayerTimerMap(private val timers: Short2IntOpenHashMap = Short2IntOpenHashMap()) :
    Iterable<Map.Entry<Short, Int>> {
    public val isNotEmpty: Boolean
        get() = timers.isNotEmpty()

    public val isEmpty: Boolean
        get() = !isNotEmpty

    public fun getOrDefault(timer: TimerType, default: Int): Int =
        timers.getOrDefault(timer.id.toShort(), default)

    // Should only be used internally by the system responsible for executing expired timers.
    public operator fun minusAssign(id: Short) {
        timers.remove(id)
    }

    public operator fun set(timer: TimerType, value: Int) {
        timers[timer.id.toShort()] = value
    }

    override fun iterator(): Iterator<Map.Entry<Short, Int>> = timers.iterator()
}
