package org.rsmod.api.player.stat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.api.testing.factory.player.TestPlayerFactory
import org.rsmod.api.testing.factory.stat.TestStatTypeFactory
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.events.EventBus
import org.rsmod.events.KeyedEventBus
import org.rsmod.events.SuspendEventBus
import org.rsmod.events.UnboundEventBus
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.stat.StatType

class PlayerSkillXPTest {
    @Test
    fun `add over max xp`() {
        val stat = TestStatTypeFactory().create(id = 0)
        val player = TestPlayerFactory().create()
        check(player.statMap.getBaseLevel(stat).toInt() == 1)

        val addXp = 500_000_000.0
        val expectedXp = PlayerStatMap.MAX_XP
        PlayerSkillXP.internalAddXP(player, stat, addXp, rate = 1.0, eventBus(), invisLvls())

        assertEquals(expectedXp, player.statMap.getXP(stat))
    }

    @TestWithArgs(StatXPProvider::class)
    fun `add stat xp`(xp: Double, xpRate: Double, expectedLvl: Int, stat: StatType) {
        val player = TestPlayerFactory().create()
        check(player.statMap.getBaseLevel(stat).toInt() == 1)

        PlayerSkillXP.internalAddXP(player, stat, xp, xpRate, eventBus(), invisLvls())

        val expectedXp = xp * xpRate
        assertEquals(expectedXp, player.statMap.getXP(stat).toDouble())
        assertEquals(expectedLvl, player.statMap.getBaseLevel(stat).toInt())
        assertEquals(expectedLvl, player.statMap.getCurrentLevel(stat).toInt())
    }

    private object StatXPProvider : TestArgsProvider {
        private val attack = TestStatTypeFactory().create(id = 0)
        private val defence = TestStatTypeFactory().create(id = 1)
        private val strength = TestStatTypeFactory().create(id = 2)

        override fun args(): List<TestArgs> =
            listOf(
                TestArgs(80.0, 1.0, 1, attack),
                TestArgs(50_000.0, 1.0, 42, defence),
                TestArgs(50_000.0, 5.0, 59, defence),
                TestArgs(6_750_000.0, 1.0, 92, strength),
                TestArgs(200_000_000, 1.0, 99, attack),
            )
    }

    private fun eventBus() = EventBus(UnboundEventBus(), KeyedEventBus(), SuspendEventBus())

    private fun invisLvls() = InvisibleLevels(emptySet())
}
