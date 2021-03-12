package org.rsmod.game.event

import org.rsmod.game.event.action.EventAction
import org.rsmod.game.event.action.EventActionBuilder
import kotlin.reflect.KClass

class EventBus(
    val events: MutableMap<KClass<out Event>, MutableList<EventAction<*>>> = mutableMapOf()
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
