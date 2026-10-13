package org.runerealm.content.interfaces.settings

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.testing.GameTestState
import org.runerealm.content.interfaces.settings.configs.setting_components
import org.runerealm.content.interfaces.settings.scripts.SettingsSideScript
import org.runerealm.game.type.interf.IfButtonOp

class SettingsCloseTest {
    @Test
    fun GameTestState.`close button closes all settings`() =
        runGameTest(SettingsSideScript::class) {
            player.ifOpenOverlay(interfaces.settings, components.toplevel_target_floater)
            assertTrue(player.ui.containsOverlay(interfaces.settings))
            player.ifButton(setting_components.settings_close)
            advance(ticks = 1)
            assertFalse(player.ui.containsOverlay(interfaces.settings))
        }

    @Test
    fun GameTestState.`escape key closes all settings`() =
        runGameTest(SettingsSideScript::class) {
            player.ifOpenOverlay(interfaces.settings, components.toplevel_target_floater)
            player.ifButton(setting_components.settings_close, op = IfButtonOp.Op2)
            advance(ticks = 1)
            assertFalse(player.ui.containsOverlay(interfaces.settings))
        }
}
