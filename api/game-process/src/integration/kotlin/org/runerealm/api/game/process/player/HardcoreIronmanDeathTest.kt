package org.runerealm.api.game.process.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.death.plugin.PlayerDeathScript
import org.runerealm.api.player.ironman.HARDCORE_DEATH_MESSAGE
import org.runerealm.api.player.ironman.IronmanMode
import org.runerealm.api.player.ironman.ironmanMode
import org.runerealm.api.player.queueDeath
import org.runerealm.api.testing.GameTestState

class HardcoreIronmanDeathTest {
    @Test
    fun GameTestState.`hardcore ironman becomes standard ironman on death`() =
        runGameTest(PlayerDeathScript::class) {
            player.ironmanMode = IronmanMode.Hardcore
            player.queueDeath()
            // Messages are captured per tick, so stop on the tick the death sequence ends.
            advanceUntil(predicate = { player.ironmanMode != IronmanMode.Hardcore })
            assertEquals(IronmanMode.Standard, player.ironmanMode)
            assertEquals(1, player.vars[varbits.ironman_hardcore_dead])
            assertMessageSent(HARDCORE_DEATH_MESSAGE)
        }

    @Test
    fun GameTestState.`standard ironman keeps mode on death`() =
        runGameTest(PlayerDeathScript::class) {
            player.ironmanMode = IronmanMode.Standard
            player.queueDeath()
            advance(ticks = 10)
            assertEquals(IronmanMode.Standard, player.ironmanMode)
            assertEquals(0, player.vars[varbits.ironman_hardcore_dead])
        }
}
