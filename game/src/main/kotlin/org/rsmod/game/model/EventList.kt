package org.rsmod.game.model

import org.rsmod.game.events.Event
import org.rsmod.game.events.EventBus
import org.rsmod.game.events.KeyedEvent

public class EventList<T> {

    private val _unbound: MutableList<Event<T>> = mutableListOf()
    private val _keyed: MutableList<CachedKeyedEvent<T>> = mutableListOf()

    public val unbound: List<Event<T>> get() = _unbound
    public val keyed: List<CachedKeyedEvent<T>> get() = _keyed

    public fun publishAll(parameter: T, eventBus: EventBus) {
        _unbound.forEach { event -> eventBus.publish(parameter, event) }
        _keyed.forEach { (id, event) -> eventBus.publish(id, parameter, event) }
    }

    public fun clear() {
        _unbound.clear()
        _keyed.clear()
    }

    public fun add(id: Long, event: KeyedEvent<T>) {
        _keyed += CachedKeyedEvent(id, event)
    }

    public operator fun plusAssign(event: Event<T>) {
        _unbound += event
    }

    public data class CachedKeyedEvent<T>(
        public val id: Long,
        public val event: KeyedEvent<T>
    )
}
