package org.rsmod.content.interfaces.emotes

import org.junit.jupiter.api.Test
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.testing.GameTestState

class EmotesScriptTest {
    @Test
    fun GameTestState.`validate all emotes`() =
        runGameTest(EmotesScript::class) {
            // Open emotes tab, otherwise button clicks would be discarded.
            player.ifOpenOverlay(interfaces.emote, components.toplevel_target_emote)

            // Ensure that buttons are actually reaching the emote code.
            val error =
                player.assertThrows<NotImplementedError> {
                    player.ifButton(emote_components.emote_list, -1)
                    advance()
                }
            assertEquals("Emote not implemented: Emote", error.message)

            val emoteSlots = cacheTypes.enums[emote_enums.emote_names].keys
            for (slot in emoteSlots) {
                assertDoesNotThrow("Emote for slot is not implemented: slot=$slot") {
                    player.ifButton(emote_components.emote_list, slot)
                    advance()
                }
            }
        }
}
