package org.rsmod.events

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList

public class UnboundEventBus {
    public val events: MutableMap<Class<out UnboundEvent>, MutableList<UnboundEvent.() -> Unit>> =
        Object2ObjectOpenHashMap<Class<out UnboundEvent>, MutableList<UnboundEvent.() -> Unit>>()

    public operator fun <T : UnboundEvent> contains(type: Class<T>): Boolean =
        events.containsKey(type)

    public operator fun <T : UnboundEvent> get(type: Class<out T>): List<T.() -> Unit>? =
        events.getOrDefault(type, null)

    public inline operator fun <reified T : UnboundEvent> plusAssign(noinline event: T.() -> Unit) {
        add(T::class.java, event)
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T : UnboundEvent> add(type: Class<T>, action: T.() -> Unit) {
        val map = events.getOrPut(type) { ObjectArrayList() }
        map += action as UnboundEvent.() -> Unit
    }
}
