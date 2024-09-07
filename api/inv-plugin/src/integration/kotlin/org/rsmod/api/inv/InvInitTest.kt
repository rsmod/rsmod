package org.rsmod.api.inv

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.GameTestState

class InvInitTest {
    @Test
    fun GameTestState.`init default player invs`() = runGameTest {
        val initializer = InvInit(cacheTypes.invs)
        val defaults = initializer.defaultInvs
        withPlayer {
            assertEquals(defaults.size, invMap.size)
            for (default in defaults) {
                assertNotNull(invMap[default])
            }
        }
    }

    @Test
    fun GameTestState.`cache common player invs`() = runGameTest {
        withPlayer {
            assertTrue(invMap.isNotEmpty())
            assertDoesNotThrow { inv[0] }
            assertDoesNotThrow { worn[0] }
        }
    }
}
