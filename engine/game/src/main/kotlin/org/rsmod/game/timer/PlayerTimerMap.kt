package org.rsmod.game.timer

import it.unimi.dsi.fastutil.objects.ObjectIterator
import it.unimi.dsi.fastutil.shorts.Short2LongMap
import it.unimi.dsi.fastutil.shorts.Short2LongOpenHashMap
import it.unimi.dsi.fastutil.shorts.ShortArraySet
import org.rsmod.annotations.InternalApi
import org.rsmod.game.type.timer.TimerType

public class PlayerTimerMap(private val timers: Short2LongOpenHashMap = Short2LongOpenHashMap()) :
    Iterable<Short2LongMap.Entry> {
    @InternalApi public val expiredKeysBuffer: ShortArraySet = ShortArraySet()

    public val isNotEmpty: Boolean
        get() = timers.isNotEmpty()

    public fun remove(timer: TimerType) {
        timers.remove(timer.id.toShort())
    }

    @OptIn(InternalApi::class)
    public fun schedule(timer: TimerType, mapClock: Int, interval: Int) {
        put(timer.id.toShort(), mapClock, interval)
    }

    @InternalApi
    public fun put(timer: Short, mapClock: Int, interval: Int) {
        val expiry = mapClock + interval
        timers[timer] = (expiry.toLong() shl 32) or interval.toLong()
    }

    @InternalApi public fun extractExpiry(packed: Long): Int = (packed shr 32).toInt()

    @InternalApi public fun extractInterval(packed: Long): Int = packed.toInt()

    @InternalApi
    public operator fun get(timerType: Short): Long? {
        val value = timers.get(timerType)
        return value.takeIf { it != timers.defaultReturnValue() }
    }

    override fun iterator(): ObjectIterator<Short2LongMap.Entry> {
        return timers.short2LongEntrySet().fastIterator()
    }

    override fun toString(): String = timers.toString()
}
