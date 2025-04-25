package org.rsmod.game.timer

import it.unimi.dsi.fastutil.objects.ObjectIterator
import it.unimi.dsi.fastutil.objects.ObjectIterators
import it.unimi.dsi.fastutil.shorts.Short2LongMap
import it.unimi.dsi.fastutil.shorts.Short2LongOpenHashMap
import it.unimi.dsi.fastutil.shorts.ShortArraySet
import org.rsmod.annotations.InternalApi
import org.rsmod.game.type.timer.TimerType

public class NpcTimerMap(private var timers: Short2LongOpenHashMap? = null) :
    Iterable<Short2LongMap.Entry> {
    @InternalApi
    public val expiredKeysBuffer: MutableSet<Short> by
        lazy(LazyThreadSafetyMode.NONE) { ShortArraySet() }

    public val isNotEmpty: Boolean
        get() = timers?.isNotEmpty() == true

    public fun remove(timer: TimerType) {
        timers?.remove(timer.id.toShort())
    }

    @OptIn(InternalApi::class)
    public fun schedule(timer: TimerType, interval: Int) {
        put(timer.id.toShort(), clockCounter = 0, interval = interval)
    }

    @InternalApi
    public fun put(timerType: Short, clockCounter: Int, interval: Int) {
        val timers = getOrCreate()
        timers[timerType] = packValues(clockCounter, interval)
    }

    @OptIn(InternalApi::class)
    private fun getOrCreate(): Short2LongOpenHashMap {
        val timers = timers ?: Short2LongOpenHashMap()
        if (this.timers == null) {
            this.timers = timers
        }
        return timers
    }

    @InternalApi public fun extractClockCounter(packed: Long): Int = (packed shr 32).toInt()

    @InternalApi public fun extractInterval(packed: Long): Int = packed.toInt()

    @InternalApi
    public fun packValues(clockCounter: Int, interval: Int): Long {
        return (clockCounter.toLong() shl 32) or interval.toLong()
    }

    @InternalApi
    public operator fun get(timerType: Short): Long? {
        val timers = this.timers ?: return null
        val value = timers.get(timerType)
        return value.takeIf { it != timers.defaultReturnValue() }
    }

    override fun iterator(): ObjectIterator<Short2LongMap.Entry> {
        return timers?.short2LongEntrySet()?.fastIterator() ?: ObjectIterators.emptyIterator()
    }

    override fun toString(): String = timers?.toString() ?: "null"
}
