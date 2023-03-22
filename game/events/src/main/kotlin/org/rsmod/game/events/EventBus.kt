package org.rsmod.game.events

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

private typealias EventClass = Class<out Event<*>>
private typealias EventAction<P1, P2> = P1.(P2) -> Unit
private typealias EventActionList = MutableList<EventAction<*, *>>

private typealias KeyedClass = Class<out KeyedEvent<*>>
private typealias EventActionMap = Long2ObjectMap<EventAction<*, *>>

public class EventBus {

    public val unboundEvents: MutableMap<EventClass, EventActionList> = mutableMapOf()
    public val keyedEvents: MutableMap<KeyedClass, EventActionMap> = mutableMapOf()

    public fun <K, T : Event<out K>> add(type: Class<T>, action: K.(T) -> Unit) {
        val list = unboundEvents.computeIfAbsent(type) { mutableListOf() }
        list += action
    }

    public fun <K, T : KeyedEvent<K>> set(id: Long, type: Class<T>, action: K.(T) -> Unit) {
        val map = keyedEvents.computeIfAbsent(type) { Long2ObjectOpenHashMap() }
        map[id] = action
    }

    public fun <T : Event<Unit>> publish(event: T) {
        get(event::class.java)?.forEach { it.invoke(Unit, event) }
    }

    public fun <K, T : Event<K>> publish(parameter: K, event: T) {
        get(event::class.java)?.forEach { it.invoke(parameter, event) }
    }

    public fun <K, T : KeyedEvent<K>> publish(id: Long, parameter: K, event: T) {
        get(event::class.java)?.get(id)?.invoke(parameter, event)
    }

    public fun <T : KeyedEvent<*>> contains(id: Long, type: Class<T>): Boolean =
        keyedEvents[type]?.containsKey(id) ?: false

    public operator fun <T : Event<*>> contains(type: Class<T>): Boolean =
        unboundEvents.containsKey(type)

    @Suppress("UNCHECKED_CAST")
    public operator fun <K, T : Event<K>> get(type: Class<out T>): List<K.(T) -> Unit>? =
        unboundEvents[type] as? List<K.(T) -> Unit>

    @Suppress("UNCHECKED_CAST")
    public operator fun <K, T : KeyedEvent<K>> get(type: Class<out T>): Map<Long, K.(T) -> Unit>? =
        keyedEvents[type] as? Map<Long, K.(T) -> Unit>
}
