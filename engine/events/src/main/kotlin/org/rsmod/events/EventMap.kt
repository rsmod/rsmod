package org.rsmod.events

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

public sealed class EventMap<K, V> {
    internal val events: Object2ObjectOpenHashMap<Class<out K>, V> = Object2ObjectOpenHashMap()

    internal operator fun contains(type: Class<out Any>): Boolean = events.contains(type)
}

public class UnboundEventMap : EventMap<UnboundEvent, MutableList<UnboundEvent.() -> Unit>>() {
    public operator fun <T : UnboundEvent> get(type: Class<out T>): List<T.() -> Unit>? {
        return events[type]
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T : UnboundEvent> add(type: Class<out T>, action: T.() -> Unit) {
        val list = events.getOrPut(type) { mutableListOf() }
        list.add(action as UnboundEvent.() -> Unit)
    }
}
