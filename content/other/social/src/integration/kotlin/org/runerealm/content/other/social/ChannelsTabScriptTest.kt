package org.runerealm.content.other.social

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.testing.GameTestState

class ChannelsTabScriptTest {
    @Test
    fun GameTestState.`channel tabs show their panels`() =
        runGameTest(ChannelsTabScript::class) {
            player.ifOpenOverlay(interfaces.side_channels, components.toplevel_target_side7)
            assertTrue(player.ui.containsOverlay(channels_interfaces.chatchannel_current))

            player.ifButton(channels_components.tab_1)
            advance()
            assertTrue(player.ui.containsOverlay(channels_interfaces.clans_sidepanel))

            player.ifButton(channels_components.tab_3)
            advance()
            assertTrue(player.ui.containsOverlay(channels_interfaces.grouping))
        }
}
