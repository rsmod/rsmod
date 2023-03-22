package org.rsmod.plugins.api

import org.rsmod.game.events.Event
import org.rsmod.game.events.EventBus
import org.rsmod.game.events.KeyedEvent

public inline fun <K, reified T : Event<K>> EventBus.subscribe(noinline action: K.(T) -> Unit): Unit =
    add(T::class.java, action)

public inline fun <K, reified T : KeyedEvent<K>> EventBus.subscribe(id: Number, noinline action: K.(T) -> Unit) {
    set(id.toLong(), T::class.java, action)
}
