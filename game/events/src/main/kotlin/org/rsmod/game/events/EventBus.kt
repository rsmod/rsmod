package org.rsmod.game.events

private typealias EventAction<T> = (T).() -> Unit

public class EventBus(
    public val internalMap: MutableMap<Class<out Event>, MutableList<EventAction<*>>> = mutableMapOf()
) : Map<Class<out Event>, List<EventAction<*>>> by internalMap {

    /**
     * Alias for [publish] method.
     */
    public inline operator fun <reified T : Event> plusAssign(event: T): Unit = publish(event)

    @Suppress("UNCHECKED_CAST")
    public inline fun <reified T : Event> publish(event: T) {
        val actions = internalMap[event::class.java] as? List<EventAction<T>> ?: return
        actions.forEach { it.invoke(event) }
    }

    public inline fun <reified T : Event> subscribe(noinline execute: (T).() -> Unit) {
        val actions = internalMap.computeIfAbsent(T::class.java) { mutableListOf() }
        actions += execute
    }
}
