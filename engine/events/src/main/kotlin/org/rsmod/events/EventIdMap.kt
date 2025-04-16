package org.rsmod.events

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

public sealed class EventIdMap<K, V> : EventMap<K, MutableMap<Long, V>>() {
    internal fun contains(type: Class<out K>, key: Long): Boolean {
        return events[type]?.containsKey(key) == true
    }

    protected fun getOrPutIdMap(type: Class<out K>): MutableMap<Long, V> {
        return events.getOrPut(type) { defaultMap() }
    }

    private fun defaultMap(): Long2ObjectOpenHashMap<V> {
        return Long2ObjectOpenHashMap<V>().apply { defaultReturnValue(null) }
    }
}

public class KeyedEventMap : EventIdMap<KeyedEvent, KeyedEvent.() -> Unit>() {
    public operator fun <T : KeyedEvent> get(type: Class<out T>, key: Int): (T.() -> Unit)? {
        return get(type, key.toLong())
    }

    @Suppress("UNCHECKED_CAST")
    public operator fun <T : KeyedEvent> get(type: Class<out T>, key: Long): (T.() -> Unit)? {
        return events[type]?.get(key) as? (T.() -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T : KeyedEvent> putIfAbsent(
        type: Class<out T>,
        key: Long,
        action: T.() -> Unit,
    ): (KeyedEvent.() -> Unit)? {
        val map = getOrPutIdMap(type)
        return map.putIfAbsent(key, action as (KeyedEvent.() -> Unit))
    }
}

private typealias EventHandler<R, T> = suspend R.(T) -> Unit

public class SuspendEventMap : EventIdMap<SuspendEvent<*>, EventHandler<*, *>>() {
    public operator fun <R, T : SuspendEvent<R>> get(
        type: Class<out T>,
        key: Int,
    ): EventHandler<R, T>? = get(type, key.toLong())

    @Suppress("UNCHECKED_CAST")
    public operator fun <R, T : SuspendEvent<R>> get(
        type: Class<out T>,
        key: Long,
    ): EventHandler<R, T>? = events[type]?.get(key) as? EventHandler<R, T>

    public fun <R, T : SuspendEvent<R>> putIfAbsent(
        type: Class<T>,
        key: Long,
        action: suspend R.(T) -> Unit,
    ): EventHandler<*, *>? {
        val map = getOrPutIdMap(type)
        return map.putIfAbsent(key, action)
    }
}
