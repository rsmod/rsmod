package org.rsmod.plugins.testing

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.info.ExtendedInfo

public inline fun <reified T : ExtendedInfo> Player.verify(predicate: (T) -> Boolean) {
    val found = extendedInfo.pendingInfo[T::class.java] as? T
    Assertions.assertNotNull(found) { "`${T::class.simpleName}` not found in extended-info pending map." }
    Assertions.assertTrue(found?.let { predicate(it) } ?: false)
}

public inline fun <reified T : ExtendedInfo> Player.verifyNull() {
    assertFalse(T::class.java in extendedInfo.pendingTypes)
}
