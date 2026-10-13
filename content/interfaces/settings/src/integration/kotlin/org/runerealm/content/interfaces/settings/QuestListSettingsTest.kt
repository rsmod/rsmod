package org.runerealm.content.interfaces.settings

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.ui.ifCloseOverlay
import org.runerealm.api.testing.GameTestState
import org.runerealm.content.interfaces.settings.scripts.QuestListSettingsScript
import org.runerealm.content.interfaces.settings.scripts.questlist_settings_components
import org.runerealm.content.interfaces.settings.scripts.questlist_settings_varbits

class QuestListSettingsTest {
    @Test
    fun GameTestState.`settings button opens all settings searched for the quest list`() =
        runGameTest(QuestListSettingsScript::class) {
            player.ifOpenOverlay(interfaces.questlist, components.toplevel_target_side2)
            player.ifButton(questlist_settings_components.settings_button)
            advance()
            assertTrue(player.ui.containsOverlay(interfaces.settings))
            assertEquals(1, player.vars[questlist_settings_varbits.floater_is_searching])

            player.ifCloseOverlay(interfaces.settings, eventBus)
            assertEquals(0, player.vars[questlist_settings_varbits.floater_is_searching])
        }
}
