package org.rsmod.content.interfaces.emotes

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.GameTestState

class EmotesConfigTest {
    @Test
    fun GameTestState.`ensure all stats have required skillcape emotes`() {
        val anims = cacheTypes.enums[emote_enums.skill_cape_anims]
        val spots = cacheTypes.enums[emote_enums.skill_cape_spots]
        val stats = cacheTypes.stats.filterValues { !it.unreleased }
        for (stat in stats.values) {
            assertTrue(stat in anims) { "Seq for skill cape emote is missing: stat=$stat" }
            assertTrue(stat in spots) { "Spotanim for skill cape emote is missing: stat=$stat" }
        }
    }
}
