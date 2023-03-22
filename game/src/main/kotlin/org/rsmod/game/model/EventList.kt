package org.rsmod.game.model

import org.rsmod.game.events.Event
import org.rsmod.game.events.EventBus
import org.rsmod.game.events.KeyedEvent

public class EventList<T> {

    private val events: MutableList<Event<T>> = mutableListOf()

    private val keyed: MutableList<CachedKeyedEvent<T>> = mutableListOf()

    public fun publishAll(parameter: T, eventBus: EventBus) {
        events.forEach { event -> eventBus.publish(parameter, event) }
        keyed.forEach { (id, event) -> eventBus.publish(id, parameter, event) }
    }

    public fun clear() {
        events.clear()
        keyed.clear()
    }

    public fun add(id: Long, event: KeyedEvent<T>) {
        keyed += CachedKeyedEvent(id, event)
    }

    public operator fun plusAssign(event: Event<T>) {
        events += event
    }

    public fun getUnbound(): List<Event<T>> = events

    public fun getKeyed(): List<CachedKeyedEvent<T>> = keyed

    public data class CachedKeyedEvent<T>(
        public val id: Long,
        public val event: KeyedEvent<T>
    )
}
