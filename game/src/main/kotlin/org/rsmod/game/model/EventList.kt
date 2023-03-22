package org.rsmod.game.model

import org.rsmod.game.events.Event
import org.rsmod.game.events.EventBus
import org.rsmod.game.events.KeyedEvent

public class EventList<T>(
    private val events: MutableList<Event<T>> = mutableListOf(),
    private val keyed: MutableMap<Long, KeyedEvent<T>> = mutableMapOf()
) {

    public fun publishAll(parameter: T, eventBus: EventBus) {
        events.forEach { event -> eventBus.publish(parameter, event) }
        keyed.forEach { (id, event) -> eventBus.publish(id, parameter, event) }
    }

    public fun clear() {
        events.clear()
        keyed.clear()
    }

    public operator fun plusAssign(event: Event<T>) {
        events += event
    }

    public operator fun set(id: Long, event: KeyedEvent<T>) {
        keyed[id] = event
    }
}
