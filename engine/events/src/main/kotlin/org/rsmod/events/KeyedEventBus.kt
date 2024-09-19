package org.rsmod.events

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

public class KeyedEventBus {
    public val events: MutableMap<Class<out KeyedEvent>, MutableMap<Long, KeyedEvent.() -> Unit>> =
        Object2ObjectOpenHashMap<Class<out KeyedEvent>, MutableMap<Long, KeyedEvent.() -> Unit>>()

    public fun <T : KeyedEvent> contains(type: Class<T>, key: Long): Boolean =
        this[type]?.containsKey(key) == true

    public operator fun <T : KeyedEvent> get(type: Class<T>): Map<Long, KeyedEvent.() -> Unit>? =
        events.getOrDefault(type, null)

    public operator fun <T : KeyedEvent> get(type: Class<T>, key: Long): (KeyedEvent.() -> Unit)? =
        events.getOrDefault(type, null)?.getOrDefault(key, null)

    @Suppress("UNCHECKED_CAST")
    public operator fun <T : KeyedEvent> set(type: Class<T>, key: Long, action: T.() -> Unit) {
        val map = events.getOrPut(type) { Long2ObjectOpenHashMap() }
        map[key] = action as KeyedEvent.() -> Unit
    }
}
