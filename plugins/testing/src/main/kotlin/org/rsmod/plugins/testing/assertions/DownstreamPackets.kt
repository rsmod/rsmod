package org.rsmod.plugins.testing.assertions

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.rsmod.game.model.DownstreamList
import org.rsmod.game.model.mob.Player
import org.rsmod.protocol.game.packet.DownstreamPacket
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
public fun <T : DownstreamPacket> DownstreamList.verify(type: KClass<T>, predicate: (T) -> Boolean) {
    val valid = filter { it::class == type }
    val pass = valid.any { predicate(it as T) }
    assertFalse(valid.isEmpty()) { "`${type.simpleName}` not found in downstream packet list." }
    assertTrue(pass) { "`${type.simpleName}` found but predicate failed. (${valid.size} tested packets)" }
}

public fun <T : DownstreamPacket> DownstreamList.verifyNull(type: KClass<T>) {
    val contains = any { it::class == type }
    assertFalse(contains)
}

public fun <T : DownstreamPacket> Player.verify(
    type: KClass<T>,
    predicate: (T) -> Boolean
): Unit = downstream.verify(type, predicate)

public fun <T : DownstreamPacket> Player.verifyNull(
    type: KClass<T>
): Unit = downstream.verifyNull(type)

public fun Player.verifyDownstream(
    downstream: DownstreamList = this.downstream,
    init: DownstreamPacketScope.() -> Unit
) {
    DownstreamPacketScope(downstream).apply(init)
    downstream.clear()
}

public class DownstreamPacketScope(private val downstream: DownstreamList) {

    public fun <T : DownstreamPacket> assert(type: KClass<T>, predicate: (T) -> Boolean) {
        downstream.verify(type, predicate)
    }

    public fun <T : DownstreamPacket> assertNull(type: KClass<T>) {
        downstream.verifyNull(type)
    }
}
