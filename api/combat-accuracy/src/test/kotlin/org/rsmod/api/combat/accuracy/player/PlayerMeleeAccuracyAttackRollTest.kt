package org.rsmod.api.combat.accuracy.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class PlayerMeleeAccuracyAttackRollTest {
    @TestWithArgs(LoadoutProvider::class)
    fun `calculate base attack roll with loadout`(loadout: Loadout) {
        val expectedRoll = loadout.expectedAttackRoll
        val visibleLvl = loadout.visibleAttackLvl
        val styleBonus = loadout.styleBonus
        val prayerBonus = loadout.prayerBonus
        val voidBonus = loadout.voidBonus
        val attackBonus = loadout.attackBonus

        val effectiveAttack =
            PlayerMeleeAccuracy.calculateEffectiveAttack(
                visibleAttackLvl = visibleLvl,
                styleBonus = styleBonus,
                prayerBonus = prayerBonus,
                voidBonus = voidBonus,
            )
        val attackRoll = PlayerMeleeAccuracy.calculateBaseAttackRoll(effectiveAttack, attackBonus)

        assertEquals(expectedRoll, attackRoll)
    }

    data class Loadout(
        val expectedAttackRoll: Int,
        val visibleAttackLvl: Int = 99,
        val styleBonus: Int = 8,
        val prayerBonus: Double = 1.0,
        val voidBonus: Double = 1.0,
        val attackBonus: Int = 0,
    ) {
        fun withSuperAttPotion(): Loadout {
            val add = 5 + (visibleAttackLvl * 0.15).toInt()
            return copy(visibleAttackLvl = visibleAttackLvl + add)
        }

        fun withSmellingSalts(): Loadout {
            val add = 11 + (visibleAttackLvl * 0.16).toInt()
            return copy(visibleAttackLvl = visibleAttackLvl + add)
        }

        fun withVoid() = copy(voidBonus = 1.1)

        fun withAccurateStyle() = copy(styleBonus = 11)

        fun withControlledStyle() = copy(styleBonus = 9)

        fun withDefensiveStyle() = copy(styleBonus = 8)

        fun withAttackBonus(bonus: Int) = copy(attackBonus = bonus)

        fun withClarityOfThought() = copy(prayerBonus = 1.05)

        fun withPiety() = copy(prayerBonus = 1.2)
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            testArgsOfSingleParam(
                /* Loadout Group */
                Loadout(expectedAttackRoll = 6848).withDefensiveStyle(),
                Loadout(expectedAttackRoll = 7040).withAccurateStyle(),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 7104).withDefensiveStyle().withClarityOfThought(),
                Loadout(expectedAttackRoll = 7296).withAccurateStyle().withClarityOfThought(),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 7488).withDefensiveStyle().withVoid(),
                Loadout(expectedAttackRoll = 7744).withAccurateStyle().withVoid(),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 8064).withDefensiveStyle().withSuperAttPotion(),
                Loadout(expectedAttackRoll = 8256).withAccurateStyle().withSuperAttPotion(),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 8832)
                    .withDefensiveStyle()
                    .withSuperAttPotion()
                    .withVoid(),
                Loadout(expectedAttackRoll = 9024)
                    .withAccurateStyle()
                    .withSuperAttPotion()
                    .withVoid(),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 9344)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withVoid(),
                Loadout(expectedAttackRoll = 9536)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withVoid(),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 11072)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety(),
                Loadout(expectedAttackRoll = 11328)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety(),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 25258)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withAttackBonus(bonus = 82),
                Loadout(expectedAttackRoll = 25404)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withAttackBonus(bonus = 82),
                Loadout(expectedAttackRoll = 25842)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withAttackBonus(bonus = 82),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 32390)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withPiety()
                    .withAttackBonus(bonus = 141),
                Loadout(expectedAttackRoll = 33005)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withPiety()
                    .withAttackBonus(bonus = 141),
            )
    }
}
