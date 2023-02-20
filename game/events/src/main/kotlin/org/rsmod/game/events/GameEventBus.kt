package org.rsmod.game.events

private typealias KGameEvent = Class<out GameEvent>
private typealias KGameKeyedEvent = Class<out GameKeyedEvent>
private typealias GameEventAction<T> = (T).() -> Unit
private typealias GameEventActionList = MutableList<GameEventAction<*>>
private typealias GameKeyedEventActionMap = MutableMap<Long, GameEventAction<*>>

public class GameEventBus {

    public val boundEvents: MutableMap<KGameKeyedEvent, GameKeyedEventActionMap> = mutableMapOf()
    public val unboundEvents: MutableMap<KGameEvent, GameEventActionList> = mutableMapOf()

    public fun <T : GameEvent> add(type: Class<T>, action: (T).() -> Unit) {
        val list = unboundEvents.computeIfAbsent(type) { mutableListOf() }
        list += action
    }

    public fun <T : GameKeyedEvent> add(type: Class<T>, id: Long, action: (T).() -> Unit) {
        val map = boundEvents.computeIfAbsent(type) { mutableMapOf() }
        map[id] = action
    }

    public fun <T : GameEvent> contains(type: Class<T>): Boolean = unboundEvents.containsKey(type)

    public fun <T : GameKeyedEvent> contains(type: Class<T>, id: Long): Boolean =
        boundEvents[type]?.containsKey(id) ?: false

    @Suppress("UNCHECKED_CAST")
    public fun <T : GameEvent> getOrNull(type: Class<out T>): List<(T).() -> Unit>? =
        unboundEvents[type] as? List<(T).() -> Unit>

    @Suppress("UNCHECKED_CAST")
    public fun <T : GameKeyedEvent> getOrNull(type: Class<out T>): Map<Long, (T).() -> Unit>? =
        boundEvents[type] as? Map<Long, (T).() -> Unit>

    public inline fun <reified T : GameEvent> subscribe(noinline action: (T).() -> Unit) {
        add(T::class.java, action)
    }

    public inline fun <reified T : GameKeyedEvent> subscribe(id: Long, noinline action: (T).() -> Unit) {
        add(T::class.java, id, action)
    }

    public inline fun <reified T : GameKeyedEvent> subscribe(id: Int, noinline action: (T).() -> Unit) {
        add(T::class.java, id.toLong(), action)
    }

    public fun <T : GameEvent> publish(event: T) {
        getOrNull(event::class.java)?.forEach { it.invoke(event) }
    }
}
