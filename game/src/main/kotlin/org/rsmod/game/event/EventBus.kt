package org.rsmod.game.event

import kotlin.reflect.KClass

private typealias EventMap = MutableMap<KClass<out Event>, MutableList<EventAction<*>>>

class EventBus(
    val events: EventMap = mutableMapOf()
) : Map<KClass<out Event>, List<EventAction<*>>> by events {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Event> publish(event: T): Boolean {
        val events = (events[event::class] as? List<EventAction<T>>) ?: return false
        val filtered = events.filter { it.where(event) }
        filtered.forEach {
            it.then(event)
        }
        return filtered.isNotEmpty()
    }

    inline fun <reified T : Event> subscribe(): EventActionBuilder<T> =
        EventActionBuilder(events.computeIfAbsent(T::class) { mutableListOf() })
}

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class EventActionBuilder<T : Event>(private val events: MutableList<EventAction<*>>) {

    private var where: (T).() -> Boolean = { true }

    fun where(where: (T).() -> Boolean): EventActionBuilder<T> {
        this.where = where
        return this
    }

    fun then(then: (T).() -> Unit) {
        events.add(EventAction(where, then))
    }
}
