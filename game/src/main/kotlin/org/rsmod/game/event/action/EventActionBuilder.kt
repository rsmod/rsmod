package org.rsmod.game.event.action

import org.rsmod.game.event.Event

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
