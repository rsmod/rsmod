package org.runerealm.content.interfaces.bank.scripts

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.testing.GameTestState
import org.runerealm.content.interfaces.bank.configs.BankInterfaces
import org.runerealm.content.interfaces.bank.configs.bank_components

class BankTutorialScriptTest {
    @Test
    fun GameTestState.`close button ends the tutorial`() =
        runGameTest(BankTutorialScript::class) {
            openBank(player)
            player.ifButton(bank_components.tutorial_button)
            advance()
            assertTrue(player.ui.containsOverlay(BankInterfaces.tutorial_overlay))

            player.resumePauseButton(bank_components.tutorial_close_button)
            advance(ticks = 2)
            assertFalse(player.ui.containsOverlay(BankInterfaces.tutorial_overlay))
        }

    @Test
    fun GameTestState.`finish on the last page ends the tutorial`() =
        runGameTest(BankTutorialScript::class) {
            openBank(player)
            player.ifButton(bank_components.tutorial_button)
            advance()
            repeat(20) {
                player.ifButton(bank_components.tutorial_next_page, comsub = 9)
                advance()
            }
            assertFalse(player.ui.containsOverlay(BankInterfaces.tutorial_overlay))
        }
}
