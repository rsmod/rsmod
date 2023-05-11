package org.rsmod.plugins.testing.assertions

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.rsmod.game.model.mob.Player
import org.rsmod.protocol.game.packet.DownstreamPacket
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
public fun <T : DownstreamPacket> Player.verify(type: KClass<T>, predicate: (T) -> Boolean) {
    val valid = downstream.filter { it::class == type }
    val pass = valid.any { predicate(it as T) }
    assertFalse(valid.isEmpty()) { "`${type.simpleName}` not found in downstream packet list." }
    assertTrue(pass) { "`${type.simpleName}` found but predicate failed. (${valid.size} tested packets)" }
}

public fun <T : DownstreamPacket> Player.verifyNull(type: KClass<T>) {
    val contains = downstream.any { it::class == type }
    assertFalse(contains)
}
