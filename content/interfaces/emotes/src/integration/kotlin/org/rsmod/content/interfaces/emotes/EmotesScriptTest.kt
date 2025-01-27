package org.rsmod.content.interfaces.emotes

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.testing.GameTestState

class EmotesScriptTest {
    @Test
    fun GameTestState.`validate all emotes`() =
        runGameTest(EmotesScript::class) {
            // Open emotes tab, otherwise button clicks would be discarded.
            player.ifOpenOverlay(interfaces.emote_tab, components.emote_tab_target)

            // Ensure that buttons are actually reaching the emote code.
            assertThrows<NotImplementedError> {
                player.handleIfButton(emote_components.emote_list, -1)
            }

            val emoteSlots = cacheTypes.enums[emote_enums.emote_names].keys
            for (slot in emoteSlots) {
                assertDoesNotThrow { player.handleIfButton(emote_components.emote_list, slot) }
            }
        }
}
