package gg.rsmod.game.timer

import kotlin.math.max

class TimerKey(
    val persistenceKey: String? = null,
    val tickOffline: Boolean = true
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimerKey

        return if (persistenceKey != null) {
            other.persistenceKey == persistenceKey
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return persistenceKey?.hashCode() ?: super.hashCode()
    }
}

class TimerMap(
    private val timers: MutableMap<TimerKey, Int> = mutableMapOf()
) : Iterable<Map.Entry<TimerKey, Int>> {

    val size: Int
        get() = timers.size

    fun isEmpty(): Boolean = timers.isEmpty()

    fun isNotEmpty(): Boolean = timers.isNotEmpty()

    fun isActive(key: TimerKey): Boolean = (get(key) ?: 0) > 0

    fun isNotActive(key: TimerKey): Boolean = !isActive(key)

    fun decrement(key: TimerKey, amount: Int = 1): Boolean {
        val left = get(key) ?: return false
        this[key] = max(0, left - amount)
        return true
    }

    fun contains(key: TimerKey): Boolean {
        return timers.contains(key)
    }

    fun remove(key: TimerKey): Int? {
        return timers.remove(key)
    }

    fun getValue(key: TimerKey): Int {
        return timers.getValue(key)
    }

    operator fun set(key: TimerKey, ticks: Int?) {
        if (ticks != null) {
            timers[key] = ticks
        } else {
            timers.remove(key)
        }
    }

    operator fun get(key: TimerKey): Int? {
        return timers[key]
    }

    override fun iterator(): MutableIterator<Map.Entry<TimerKey, Int>> {
        return timers.iterator()
    }
}
