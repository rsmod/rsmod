package org.rsmod.events

public class EventBus(
    public val unbound: UnboundEventBus = UnboundEventBus(),
    public val keyed: KeyedEventBus = KeyedEventBus(),
    public val suspend: SuspendEventBus = SuspendEventBus(),
) {
    /* Keyed events */
    public fun <T : KeyedEvent> publish(event: T): Boolean {
        val map = keyed[event::class.java] ?: return false
        val action = map[event.id] ?: return false
        action(event)
        return true
    }

    public inline fun <reified T : KeyedEvent> subscribe(
        id: Number,
        noinline action: T.() -> Unit,
    ) {
        if (keyed.contains(T::class.java, id.toLong())) {
            error("Event type `${T::class.java.simpleName}` with id `$id` was already subscribed.")
        }
        keyed[T::class.java, id.toLong()] = action
    }

    /* Suspend events */
    public fun <T : SuspendEvent<*>> contains(type: Class<T>, key: Number): Boolean =
        suspend.contains(type, key.toLong())

    public fun <K, T : SuspendEvent<K>> subscribe(
        type: Class<T>,
        id: Number,
        action: suspend K.(T) -> Unit,
    ) {
        if (suspend.contains(type, id.toLong())) {
            error("Event type `${type.simpleName}` with id `$id` was already subscribed.")
        }
        suspend.set(type, id.toLong(), action)
    }

    public suspend fun <K, T : SuspendEvent<K>> publish(receiver: K, event: T): Boolean {
        val map = suspend[event::class.java] ?: return false
        val action = map[event.id] ?: return false
        action(receiver, event)
        return true
    }

    /* Unbound events */
    public fun <T : UnboundEvent> contains(type: Class<T>): Boolean = unbound.contains(type)

    public fun <T : UnboundEvent> publish(event: T): Boolean {
        val actions = unbound[event::class.java] ?: return false
        actions.forEach { action -> action(event) }
        return true
    }

    public inline fun <reified T : UnboundEvent> subscribe(noinline action: T.() -> Unit) {
        unbound.add(T::class.java, action)
    }
}
