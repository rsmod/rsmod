@file:Suppress("UNCHECKED_CAST")

package org.rsmod.plugins.testing.assertions

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.rsmod.game.events.KeyedEvent
import org.rsmod.game.model.EventList
import org.rsmod.game.model.mob.Player
import kotlin.reflect.KClass

public fun <T : KeyedEvent<Player>> Player.assertAny(
    type: KClass<T>,
    predicate: Player.(T) -> Boolean
): Unit = events.assertAny(this, type, predicate)

public fun <T : KeyedEvent<Player>> Player.assertFirst(
    type: KClass<T>,
    predicate: Player.(T) -> Boolean
): Unit = events.assertFirst(this, type, predicate)

public fun <T : KeyedEvent<Player>> Player.assertLast(
    type: KClass<T>,
    predicate: Player.(T) -> Boolean
): Unit = events.assertLast(this, type, predicate)

public fun <T : KeyedEvent<Player>> Player.assertNull(type: KClass<T>): Unit =
    events.assertNull(type)

public fun <T : KeyedEvent<Player>> Player.assertNotNull(type: KClass<T>): Unit =
    events.assertNotNull(type)

private fun <K, T : KeyedEvent<K>> EventList<K>.assertAny(
    context: K,
    type: KClass<T>,
    predicate: K.(T) -> Boolean
) {
    val valid = keyed.filter { it.event::class == type }
    val pass = valid.any { predicate(context, it.event as T) }
    assertFalse(valid.isEmpty()) { "`${type.simpleName}` not found in keyed event list." }
    assertTrue(pass) { "`${type.simpleName}` found but predicate failed. (${valid.size} tested events)" }
}

private fun <K, T : KeyedEvent<K>> EventList<K>.assertFirst(
    context: K,
    type: KClass<T>,
    predicate: K.(T) -> Boolean
) {
    val first = keyed.firstOrNull { it.event::class == type }
    assertNotNull(first) { "`${type.simpleName}` not found in keyed event list." }
    val event = first?.event as? T
    checkNotNull(event) // Should _not_ be null at this point.
    val pass = predicate(context, event)
    assertTrue(pass)
}

private fun <K, T : KeyedEvent<K>> EventList<K>.assertLast(
    context: K,
    type: KClass<T>,
    predicate: K.(T) -> Boolean
) {
    val last = keyed.lastOrNull { it.event::class == type }
    assertNotNull(last) { "`${type.simpleName}` not found in keyed event list." }
    val event = last?.event as? T
    checkNotNull(event) // Should _not_ be null at this point.
    val pass = predicate(context, event)
    assertTrue(pass)
}

private fun <K, T : KeyedEvent<K>> EventList<K>.assertNull(type: KClass<T>) {
    val contains = keyed.any { it.event::class == type }
    assertFalse(contains)
}

private fun <K, T : KeyedEvent<K>> EventList<K>.assertNotNull(type: KClass<T>) {
    val contains = keyed.any { it.event::class == type }
    assertTrue(contains)
}
