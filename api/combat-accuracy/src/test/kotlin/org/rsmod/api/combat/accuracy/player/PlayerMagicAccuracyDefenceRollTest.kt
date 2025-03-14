package org.rsmod.api.combat.accuracy.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class PlayerMagicAccuracyDefenceRollTest {
    @TestWithArgs(LoadoutProvider::class)
    fun `calculate base defence roll with loadout`(loadout: Loadout) {
        val expectedRoll = loadout.expectedDefenceRoll
        val defenceLvl = loadout.visibleDefenceLvl
        val magicLvl = loadout.visibleMagicLvl
        val styleBonus = loadout.styleBonus
        val defPrayerBonus = loadout.defencePrayerBonus
        val magicPrayerBonus = loadout.magicPrayerBonus
        val armourBonus = loadout.armourBonus
        val defenceBonus = loadout.defenceBonus

        val effectiveDefence =
            PlayerMagicAccuracy.calculateEffectiveDefence(
                visibleDefenceLvl = defenceLvl,
                visibleMagicLvl = magicLvl,
                styleBonus = styleBonus,
                defencePrayerBonus = defPrayerBonus,
                magicPrayerBonus = magicPrayerBonus,
                armourBonus = armourBonus,
            )
        val defenceRoll =
            PlayerMagicAccuracy.calculateBaseDefenceRoll(effectiveDefence, defenceBonus)

        assertEquals(expectedRoll, defenceRoll)
    }

    data class Loadout(
        val expectedDefenceRoll: Int,
        val visibleDefenceLvl: Int = 99,
        val visibleMagicLvl: Int = 99,
        val styleBonus: Int = 8,
        val defencePrayerBonus: Double = 1.0,
        val magicPrayerBonus: Double = 1.0,
        val armourBonus: Double = 1.0,
        val defenceBonus: Int = 0,
    ) {
        fun withSuperDefPotion(): Loadout {
            val add = 5 + (visibleDefenceLvl * 0.15).toInt()
            return copy(visibleDefenceLvl = visibleDefenceLvl + add)
        }

        fun withMagicPotion(): Loadout = copy(visibleMagicLvl = visibleMagicLvl + 4)

        fun withSmellingSalts(): Loadout {
            val defenceAdd = 11 + (visibleDefenceLvl * 0.16).toInt()
            val magicAdd = 11 + (visibleMagicLvl * 0.16).toInt()
            return copy(
                visibleDefenceLvl = visibleDefenceLvl + defenceAdd,
                visibleMagicLvl = visibleMagicLvl + magicAdd,
            )
        }

        fun withMagicLevel(level: Int) = copy(visibleMagicLvl = level)

        fun withDefenceLevel(level: Int) = copy(visibleDefenceLvl = level)

        fun withAggressiveStyle() = copy(styleBonus = 8)

        fun withDefensiveStyle() = copy(styleBonus = 11)

        fun withDefenceBonus(bonus: Int) = copy(defenceBonus = bonus)

        fun withPiety() = copy(defencePrayerBonus = 1.25)

        fun withAugury() = copy(defencePrayerBonus = 1.25, magicPrayerBonus = 1.25)

        fun withMysticVigour() = copy(defencePrayerBonus = 1.05, magicPrayerBonus = 1.18)
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            testArgsOfSingleParam(
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 6976).withDefensiveStyle(),
                Loadout(expectedDefenceRoll = 6784).withAggressiveStyle(),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 7168).withDefensiveStyle().withMagicPotion(),
                Loadout(expectedDefenceRoll = 6976).withAggressiveStyle().withMagicPotion(),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 7360).withDefensiveStyle().withSuperDefPotion(),
                Loadout(expectedDefenceRoll = 7168).withAggressiveStyle().withSuperDefPotion(),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 2560).withDefensiveStyle().withMagicLevel(1),
                Loadout(expectedDefenceRoll = 2368).withAggressiveStyle().withMagicLevel(1),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 5120).withDefensiveStyle().withDefenceLevel(1),
                Loadout(expectedDefenceRoll = 4928).withAggressiveStyle().withDefenceLevel(1),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 4800).withDefensiveStyle().withMagicLevel(50),
                Loadout(expectedDefenceRoll = 4608).withAggressiveStyle().withMagicLevel(50),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 6080).withDefensiveStyle().withDefenceLevel(50),
                Loadout(expectedDefenceRoll = 5888).withAggressiveStyle().withDefenceLevel(50),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 9216)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withPiety(),
                Loadout(expectedDefenceRoll = 9024)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withPiety(),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 10624)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withAugury(),
                Loadout(expectedDefenceRoll = 10432)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withAugury(),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 24320)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withMysticVigour()
                    .withDefenceBonus(bonus = 96),
                Loadout(expectedDefenceRoll = 23840)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withMysticVigour()
                    .withDefenceBonus(bonus = 96),
                /* Loadout Group */
                Loadout(expectedDefenceRoll = 26560)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withAugury()
                    .withDefenceBonus(bonus = 96),
                Loadout(expectedDefenceRoll = 26080)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withAugury()
                    .withDefenceBonus(bonus = 96),
            )
    }
}
