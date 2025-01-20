package org.rsmod.api.inv.map

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.GameTestState

class InvMapInitTest {
    @Test
    fun GameTestState.`init default player invs`() = runBasicGameTest {
        val initializer = InvMapInit(cacheTypes.invs)
        val defaults = initializer.defaultInvs
        withPlayer {
            Assertions.assertEquals(defaults.size, invMap.size)
            for (default in defaults) {
                Assertions.assertNotNull(invMap[default])
            }
        }
    }

    @Test
    fun GameTestState.`cache common player invs`() = runBasicGameTest {
        withPlayer {
            Assertions.assertTrue(invMap.isNotEmpty())
            Assertions.assertDoesNotThrow { inv[0] }
            Assertions.assertDoesNotThrow { worn[0] }
        }
    }
}
