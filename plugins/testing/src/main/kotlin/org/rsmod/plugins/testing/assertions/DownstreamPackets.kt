@file:Suppress("unused", "UNCHECKED_CAST")

package org.rsmod.plugins.testing.assertions

import org.junit.jupiter.api.Assertions.assertFalse
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

public fun <T : DownstreamPacket> Player.assertTrue(type: KClass<T>, predicate: (T) -> Boolean): Unit =
    downstream.assertTrue(type, predicate)

public fun <T : DownstreamPacket> Player.assertNull(type: KClass<T>): Unit = downstream.assertNull(type)

private fun <T : DownstreamPacket> DownstreamList.assertTrue(type: KClass<T>, predicate: (T) -> Boolean) {
    val valid = filter { it::class == type }
    val pass = valid.any { predicate(it as T) }
    assertFalse(valid.isEmpty()) { "`${type.simpleName}` not found in downstream packet list." }
    assertTrue(pass) { "`${type.simpleName}` found but predicate failed. (${valid.size} tested packets)" }
}

private fun <T : DownstreamPacket> DownstreamList.assertFalse(type: KClass<T>, predicate: (T) -> Boolean) {
    val valid = filter { it::class == type }
    val pass = valid.any { predicate(it as T) }
    assertFalse(valid.isEmpty()) { "`${type.simpleName}` not found in downstream packet list." }
    assertFalse(pass) { "`${type.simpleName}` found but predicate passed. (${valid.size} tested packets)" }
}

private fun <T : DownstreamPacket> DownstreamList.assertNull(type: KClass<T>) {
    val contains = any { it::class == type }
    assertFalse(contains)
}

public class DownstreamPacketScope(private val downstream: DownstreamList) {

    public fun <T : DownstreamPacket> assertTrue(type: KClass<T>, predicate: (T) -> Boolean) {
        downstream.assertTrue(type, predicate)
    }

    public fun <T : DownstreamPacket> assertFalse(type: KClass<T>, predicate: (T) -> Boolean) {
        downstream.assertFalse(type, predicate)
    }

    public fun <T : DownstreamPacket> assertNull(type: KClass<T>) {
        downstream.assertNull(type)
    }
}
