package org.rsmod.plugins.testing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.game.model.mob.list.countNotNull

class GameTestScopeTest {

    @Test
    fun testWithPlayer() {
        val scope = GameTestScope()
        check(scope.playerList.isEmpty())
        scope.withPlayer {
            assertEquals(1, scope.playerList.countNotNull())
        }
        assertTrue(scope.playerList.isEmpty())
    }
}
