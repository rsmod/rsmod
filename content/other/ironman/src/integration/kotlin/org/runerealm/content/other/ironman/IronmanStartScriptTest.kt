package org.runerealm.content.other.ironman

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.player.ironman.IronmanMode
import org.runerealm.api.player.ironman.ironmanMode
import org.runerealm.api.testing.GameTestState
import org.runerealm.content.other.ironman.configs.ironman_interfaces

class IronmanStartScriptTest {
    @Test
    fun GameTestState.`fresh accounts are offered ironman modes after the creator`() =
        runGameTest(IronmanStartScript::class) {
            player.ifOpenMain(ironman_start_interfaces.player_design)
            player.ifClose()
            advance(ticks = 2)
            assertTrue(player.ui.containsModal(ironman_interfaces.ironman_setup))
        }

    @Test
    fun GameTestState.`ironmen are not offered modes again`() =
        runGameTest(IronmanStartScript::class) {
            player.ironmanMode = IronmanMode.Standard
            player.ifOpenMain(ironman_start_interfaces.player_design)
            player.ifClose()
            advance(ticks = 2)
            assertFalse(player.ui.containsModal(ironman_interfaces.ironman_setup))
        }
}
