package org.rsmod.api.player.stat

import org.junit.jupiter.api.Test
import org.rsmod.api.config.refs.stats
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.stat.StatType

class PlayerSkillXPTest {
    @Test
    fun GameTestState.`add over max xp`() = runGameTest {
        val stat = stats.attack
        check(player.statMap.getBaseLevel(stat).toInt() == 1)

        val addXp = 500_000_000.0
        val expectedXp = PlayerStatMap.MAX_XP
        PlayerSkillXP.internalAddXP(player, stat, addXp, rate = 1.0)

        assertEquals(expectedXp, player.statMap.getXP(stat))
    }

    @TestWithArgs(StatXPProvider::class)
    fun `add stat xp`(
        xp: Double,
        xpRate: Double,
        expectedLvl: Int,
        stat: StatType,
        state: GameTestState,
    ) {
        state.runGameTest {
            check(player.statMap.getBaseLevel(stat).toInt() == 1)

            PlayerSkillXP.internalAddXP(player, stat, xp, xpRate)

            val expectedXp = xp * xpRate
            assertEquals(expectedXp, player.statMap.getXP(stat).toDouble())
            assertEquals(expectedLvl, player.statMap.getBaseLevel(stat).toInt())
            assertEquals(expectedLvl, player.statMap.getCurrentLevel(stat).toInt())
        }
    }

    private object StatXPProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            listOf(
                TestArgs(80.0, 1.0, 1, stats.attack),
                TestArgs(50_000.0, 1.0, 42, stats.defence),
                TestArgs(50_000.0, 5.0, 59, stats.defence),
                TestArgs(6_750_000.0, 1.0, 92, stats.strength),
                TestArgs(200_000_000, 1.0, 99, stats.attack),
            )
    }
}
