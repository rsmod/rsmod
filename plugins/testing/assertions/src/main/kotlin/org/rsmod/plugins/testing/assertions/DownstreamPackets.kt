@file:Suppress("unused", "UNCHECKED_CAST")

package org.rsmod.plugins.testing.assertions

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.rsmod.game.model.DownstreamList
import org.rsmod.game.model.mob.Player
import org.rsmod.game.protocol.packet.DownstreamPacket
import kotlin.reflect.KClass

public fun Player.withDownstreamScope(
    downstream: DownstreamList = this.downstream,
    init: DownstreamPacketScope.() -> Unit
) {
    DownstreamPacketScope(downstream).apply(init)
    downstream.clear()
}

public fun <T : DownstreamPacket> Player.assertAny(type: KClass<T>, predicate: (T) -> Boolean): Unit =
    downstream.assertAny(type, predicate)

public fun <T : DownstreamPacket> Player.assertNone(type: KClass<T>, predicate: (T) -> Boolean): Unit =
    downstream.assertNone(type, predicate)

public fun <T : DownstreamPacket> Player.assertFirst(type: KClass<T>, predicate: (T) -> Boolean): Unit =
    downstream.assertFirst(type, predicate)

public fun <T : DownstreamPacket> Player.assertLast(type: KClass<T>, predicate: (T) -> Boolean): Unit =
    downstream.assertLast(type, predicate)

public fun <T : DownstreamPacket> Player.assertNull(type: KClass<T>): Unit =
    downstream.assertNull(type)

private fun <T : DownstreamPacket> DownstreamList.assertAny(
    type: KClass<T>,
    predicate: (T) -> Boolean
) {
    val valid = filter { it::class == type }
    val pass = valid.any { predicate(it as T) }
    assertFalse(valid.isEmpty()) { "`${type.simpleName}` not found in downstream packet list." }
    assertTrue(pass) { "`${type.simpleName}` found but predicate failed. (${valid.size} tested packets)" }
}

private fun <T : DownstreamPacket> DownstreamList.assertNone(
    type: KClass<T>,
    predicate: (T) -> Boolean
) {
    val valid = filter { it::class == type }
    val pass = valid.any { predicate(it as T) }
    assertFalse(valid.isEmpty()) { "`${type.simpleName}` not found in downstream packet list." }
    assertFalse(pass) { "`${type.simpleName}` found but predicate passed. (${valid.size} tested packets)" }
}

private fun <T : DownstreamPacket> DownstreamList.assertFirst(
    type: KClass<T>,
    predicate: (T) -> Boolean
) {
    val first = firstOrNull { it::class == type }
    assertNotNull(first) { "`${type.simpleName}` not found in downstream packet list." }
    val pass = predicate(first as T)
    assertTrue(pass)
}

private fun <T : DownstreamPacket> DownstreamList.assertLast(
    type: KClass<T>,
    predicate: (T) -> Boolean
) {
    val first = lastOrNull { it::class == type }
    assertNotNull(first) { "`${type.simpleName}` not found in downstream packet list." }
    val pass = predicate(first as T)
    assertTrue(pass)
}

private fun <T : DownstreamPacket> DownstreamList.assertNull(type: KClass<T>) {
    val contains = any { it::class == type }
    assertFalse(contains)
}

private fun <T : DownstreamPacket> DownstreamList.assertNotNull(type: KClass<T>) {
    val contains = any { it::class == type }
    assertTrue(contains)
}

public class DownstreamPacketScope(private val downstream: DownstreamList) {

    public fun <T : DownstreamPacket> assertAny(type: KClass<T>, predicate: (T) -> Boolean) {
        downstream.assertAny(type, predicate)
    }

    public fun <T : DownstreamPacket> assertNone(type: KClass<T>, predicate: (T) -> Boolean) {
        downstream.assertNone(type, predicate)
    }

    public fun <T : DownstreamPacket> assertFirst(type: KClass<T>, predicate: (T) -> Boolean) {
        downstream.assertFirst(type, predicate)
    }

    public fun <T : DownstreamPacket> assertLast(type: KClass<T>, predicate: (T) -> Boolean) {
        downstream.assertLast(type, predicate)
    }

    public fun <T : DownstreamPacket> assertNull(type: KClass<T>) {
        downstream.assertNull(type)
    }

    public fun <T : DownstreamPacket> assertNotNull(type: KClass<T>) {
        downstream.assertNotNull(type)
    }
}
