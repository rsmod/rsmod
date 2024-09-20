package org.rsmod.game.timer

import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap
import java.util.Collections
import org.rsmod.game.type.timer.TimerType

public class NpcTimerMap(private var timers: Short2IntOpenHashMap? = null) :
    Iterable<Map.Entry<Short, Int>> {
    public val isNotEmpty: Boolean
        get() = !isEmpty

    public val isEmpty: Boolean
        get() = timers.isNullOrEmpty()

    public fun getOrDefault(timer: TimerType, default: Int): Int =
        timers?.getOrDefault(timer.id.toShort(), default) ?: default

    // Should only be used internally by the system responsible for executing expired timers.
    public operator fun minusAssign(id: Short) {
        timers?.remove(id)
    }

    public operator fun set(timer: TimerType, value: Int) {
        val timers = getOrCreate()
        timers[timer.id.toShort()] = value
    }

    private fun getOrCreate(): Short2IntOpenHashMap {
        val timers = timers ?: Short2IntOpenHashMap()
        if (this.timers == null) {
            this.timers = timers
        }
        return timers
    }

    override fun iterator(): Iterator<Map.Entry<Short, Int>> =
        timers?.iterator() ?: Collections.emptyIterator()
}
