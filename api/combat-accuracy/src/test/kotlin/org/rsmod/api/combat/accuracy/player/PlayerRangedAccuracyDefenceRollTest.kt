package org.rsmod.api.combat.accuracy.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class PlayerRangedAccuracyDefenceRollTest {
    @TestWithArgs(LoadoutProvider::class)
    fun `calculate base defence roll with loadout`(loadout: Loadout) {
        val expectedRoll = loadout.expectedDefenceRoll
        val visibleLvl = loadout.visibleDefenceLvl
        val styleBonus = loadout.styleBonus
        val prayerBonus = loadout.prayerBonus
        val armourBonus = loadout.armourBonus
        val defenceBonus = loadout.defenceBonus

        val effectiveDefence =
            PlayerRangedAccuracy.calculateEffectiveDefence(
                visibleDefenceLvl = visibleLvl,
                styleBonus = styleBonus,
                prayerBonus = prayerBonus,
                armourBonus = armourBonus,
            )
        val defenceRoll =
            PlayerRangedAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)

        assertEquals(expectedRoll, defenceRoll)
    }

    data class Loadout(
        val expectedDefenceRoll: Int,
        val visibleDefenceLvl: Int = 99,
        val styleBonus: Int = 8,
        val prayerBonus: Double = 1.0,
        val armourBonus: Double = 1.0,
        val defenceBonus: Int = 0,
    ) {
        fun withSuperDefPotion(): Loadout {
            val add = 5 + (visibleDefenceLvl * 0.15).toInt()
            return copy(visibleDefenceLvl = visibleDefenceLvl + add)
        }

        fun withSmellingSalts(): Loadout {
            val add = 11 + (visibleDefenceLvl * 0.16).toInt()
            return copy(visibleDefenceLvl = visibleDefenceLvl + add)
        }

        fun withAggressiveStyle() = copy(styleBonus = 8)

        fun withControlledStyle() = copy(styleBonus = 9)

        fun withDefensiveStyle() = copy(styleBonus = 11)

        fun withDefenceBonus(bonus: Int) = copy(defenceBonus = bonus)

        fun withThickSkin() = copy(prayerBonus = 1.05)

        fun withPiety() = copy(prayerBonus = 1.25)
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            testArgsOfSingleParam(
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 7040).withDefensiveStyle(),
                Loadout(expectedDefenceRoll = 6848).withAggressiveStyle(),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 7296).withDefensiveStyle().withThickSkin(),
                Loadout(expectedDefenceRoll = 7104).withAggressiveStyle().withThickSkin(),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 8256).withDefensiveStyle().withSuperDefPotion(),
                Loadout(expectedDefenceRoll = 8064).withAggressiveStyle().withSuperDefPotion(),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 10688)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withPiety(),
                Loadout(expectedDefenceRoll = 10496)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withPiety(),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 51436)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withPiety()
                    .withDefenceBonus(bonus = 244),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 59452)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withPiety()
                    .withDefenceBonus(bonus = 292),
                Loadout(expectedDefenceRoll = 58740)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withPiety()
                    .withDefenceBonus(bonus = 292),
                Loadout(expectedDefenceRoll = 58384)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withPiety()
                    .withDefenceBonus(bonus = 292),
            )
    }
}
