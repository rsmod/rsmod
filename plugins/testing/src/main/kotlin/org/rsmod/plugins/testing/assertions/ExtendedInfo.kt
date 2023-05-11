package org.rsmod.plugins.testing.assertions

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.info.ExtendedInfo
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
public fun <T : ExtendedInfo> Player.verify(type: KClass<T>, predicate: (T) -> Boolean) {
    val found = extendedInfo.pendingInfo[type.java] as? T
    assertNotNull(found) { "`${type.simpleName}` not found in extended-info pending map." }
    assertTrue(found?.let { predicate(it) } ?: false)
}

public fun <T : ExtendedInfo> Player.verifyNull(type: KClass<T>) {
    assertFalse(type.java in extendedInfo.pendingTypes)
}
