package org.rsmod.game.events

public inline fun <reified T : GameEvent> GameEventBus.subscribe(noinline action: (T).() -> Unit) {
    add(T::class.java, action)
}

public inline fun <reified T : GameKeyedEvent> GameEventBus.subscribe(id: Long, noinline action: (T).() -> Unit) {
    add(T::class.java, id, action)
}

public inline fun <reified T : GameKeyedEvent> GameEventBus.subscribe(id: Int, noinline action: (T).() -> Unit) {
    add(T::class.java, id.toLong(), action)
}

public fun <T : GameEvent> GameEventBus.publish(event: T) {
    getOrNull(event::class.java)?.forEach { it.invoke(event) }
}

public fun <T : GameKeyedEvent> GameEventBus.publish(event: T, id: Long) {
    getOrNull(event::class.java)?.get(id)?.invoke(event)
}
