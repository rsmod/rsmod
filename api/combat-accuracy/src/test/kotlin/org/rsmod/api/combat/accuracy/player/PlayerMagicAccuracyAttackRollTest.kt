package org.rsmod.api.combat.accuracy.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class PlayerMagicAccuracyAttackRollTest {
    @TestWithArgs(LoadoutProvider::class)
    fun `calculate base attack roll with loadout`(loadout: Loadout) {
        val expectedRoll = loadout.expectedAttackRoll
        val visibleLvl = loadout.visibleMagicLvl
        val styleBonus = loadout.styleBonus
        val prayerBonus = loadout.prayerBonus
        val voidBonus = loadout.voidBonus
        val magicBonus = loadout.magicBonus

        val effectiveAttack =
            PlayerMagicAccuracy.calculateEffectiveMagic(
                visibleMagicLvl = visibleLvl,
                styleBonus = styleBonus,
                prayerBonus = prayerBonus,
                voidBonus = voidBonus,
            )
        val attackRoll = PlayerMagicAccuracy.calculateBaseAttackRoll(effectiveAttack, magicBonus)

        assertEquals(expectedRoll, attackRoll)
    }

    data class Loadout(
        val expectedAttackRoll: Int,
        val visibleMagicLvl: Int = 99,
        val styleBonus: Int = 8,
        val prayerBonus: Double = 1.0,
        val voidBonus: Double = 1.0,
        val magicBonus: Int = 0,
    ) {
        fun withMagicPotion(): Loadout = copy(visibleMagicLvl = visibleMagicLvl + 4)

        fun withSmellingSalts(): Loadout {
            val add = 11 + (visibleMagicLvl * 0.16).toInt()
            return copy(visibleMagicLvl = visibleMagicLvl + add)
        }

        fun withVoid() = copy(voidBonus = 1.45)

        fun withAccurateStyle() = copy(styleBonus = 11)

        fun withOtherStyle() = copy(styleBonus = 9)

        fun withMagicBonus(bonus: Int) = copy(magicBonus = bonus)

        fun withMysticWill() = copy(prayerBonus = 1.05)

        fun withAugury() = copy(prayerBonus = 1.25)
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            testArgsOfSingleParam(
                /* Loadout Group */
                Loadout(expectedAttackRoll = 8532).withOtherStyle().withMagicBonus(bonus = 15),
                Loadout(expectedAttackRoll = 8690).withAccurateStyle().withMagicBonus(bonus = 15),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 8848)
                    .withOtherStyle()
                    .withMysticWill()
                    .withMagicBonus(bonus = 15),
                Loadout(expectedAttackRoll = 9006)
                    .withAccurateStyle()
                    .withMysticWill()
                    .withMagicBonus(bonus = 15),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 12324)
                    .withOtherStyle()
                    .withVoid()
                    .withMagicBonus(bonus = 15),
                Loadout(expectedAttackRoll = 12561)
                    .withAccurateStyle()
                    .withVoid()
                    .withMagicBonus(bonus = 15),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 8848)
                    .withOtherStyle()
                    .withMagicPotion()
                    .withMagicBonus(bonus = 15),
                Loadout(expectedAttackRoll = 9006)
                    .withAccurateStyle()
                    .withMagicPotion()
                    .withMagicBonus(bonus = 15),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 12798)
                    .withOtherStyle()
                    .withMagicPotion()
                    .withVoid()
                    .withMagicBonus(bonus = 15),
                Loadout(expectedAttackRoll = 13035)
                    .withAccurateStyle()
                    .withMagicPotion()
                    .withVoid()
                    .withMagicBonus(bonus = 15),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 15326)
                    .withOtherStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withMagicBonus(bonus = 15),
                Loadout(expectedAttackRoll = 15563)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withMagicBonus(bonus = 15),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 18881)
                    .withOtherStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withAugury()
                    .withMagicBonus(bonus = 15),
                Loadout(expectedAttackRoll = 19118)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withAugury()
                    .withMagicBonus(bonus = 15),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 30831)
                    .withOtherStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withAugury()
                    .withMagicBonus(bonus = 65),
                Loadout(expectedAttackRoll = 31218)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withAugury()
                    .withMagicBonus(bonus = 65),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 34320)
                    .withOtherStyle()
                    .withSmellingSalts()
                    .withAugury()
                    .withMagicBonus(bonus = 144),
                Loadout(expectedAttackRoll = 34736)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withAugury()
                    .withMagicBonus(bonus = 144),
            )
    }
}
