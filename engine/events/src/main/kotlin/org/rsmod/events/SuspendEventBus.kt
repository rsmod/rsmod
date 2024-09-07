package org.rsmod.events

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

private typealias EventAction<T, K> = suspend T.(K) -> Unit

private typealias EventActionMap = Long2ObjectMap<EventAction<*, *>>

private typealias EventMap = Object2ObjectOpenHashMap<Class<out SuspendEvent<*>>, EventActionMap>

public class SuspendEventBus {
    public val events: EventMap = Object2ObjectOpenHashMap()

    public fun <T : SuspendEvent<*>> contains(type: Class<T>, key: Long): Boolean =
        events[type]?.containsKey(key) == true

    public fun <K, T : SuspendEvent<K>> set(
        type: Class<T>,
        key: Long,
        action: suspend K.(T) -> Unit,
    ) {
        val map = events.computeIfAbsent(type) { Long2ObjectOpenHashMap() }
        map[key] = action
    }

    @Suppress("UNCHECKED_CAST")
    public operator fun <K, T : SuspendEvent<K>> get(
        type: Class<out T>
    ): Map<Long, suspend K.(T) -> Unit>? = events[type] as? Map<Long, suspend K.(T) -> Unit>
}
