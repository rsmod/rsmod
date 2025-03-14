package org.rsmod.api.combat.accuracy.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class PlayerRangedAccuracyAttackRollTest {
    @TestWithArgs(LoadoutProvider::class)
    fun `calculate base ranged roll with loadout`(loadout: Loadout) {
        val expectedRoll = loadout.expectedAttackRoll
        val visibleLvl = loadout.visibleRangedLvl
        val styleBonus = loadout.styleBonus
        val prayerBonus = loadout.prayerBonus
        val voidBonus = loadout.voidBonus
        val rangedBonus = loadout.rangedBonus

        val effectiveRanged =
            PlayerRangedAccuracy.calculateEffectiveRanged(
                visibleRangedLvl = visibleLvl,
                styleBonus = styleBonus,
                prayerBonus = prayerBonus,
                voidBonus = voidBonus,
            )
        val attackRoll = PlayerRangedAccuracy.calculateBaseAttackRoll(effectiveRanged, rangedBonus)

        assertEquals(expectedRoll, attackRoll)
    }

    data class Loadout(
        val expectedAttackRoll: Int,
        val visibleRangedLvl: Int = 99,
        val styleBonus: Int = 8,
        val prayerBonus: Double = 1.0,
        val voidBonus: Double = 1.0,
        val rangedBonus: Int = 0,
    ) {
        fun withRangingPotion(): Loadout {
            val add = 4 + (visibleRangedLvl * 0.1).toInt()
            return copy(visibleRangedLvl = visibleRangedLvl + add)
        }

        fun withSmellingSalts(): Loadout {
            val add = 11 + (visibleRangedLvl * 0.16).toInt()
            return copy(visibleRangedLvl = visibleRangedLvl + add)
        }

        fun withVoid() = copy(voidBonus = 1.1)

        fun withAccurateStyle() = copy(styleBonus = 11)

        fun withRapidStyle() = copy(styleBonus = 8)

        fun withRangedBonus(bonus: Int) = copy(rangedBonus = bonus)

        fun withSharpEye() = copy(prayerBonus = 1.05)

        fun withRigour() = copy(prayerBonus = 1.2)
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            testArgsOfSingleParam(
                /* Loadout Group */
                Loadout(expectedAttackRoll = 7704).withRapidStyle().withRangedBonus(8),
                Loadout(expectedAttackRoll = 7920).withAccurateStyle().withRangedBonus(8),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 7992)
                    .withRapidStyle()
                    .withSharpEye()
                    .withRangedBonus(8),
                Loadout(expectedAttackRoll = 8208)
                    .withAccurateStyle()
                    .withSharpEye()
                    .withRangedBonus(8),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 8424).withRapidStyle().withVoid().withRangedBonus(8),
                Loadout(expectedAttackRoll = 8712)
                    .withAccurateStyle()
                    .withVoid()
                    .withRangedBonus(8),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 8640)
                    .withRapidStyle()
                    .withRangingPotion()
                    .withRangedBonus(8),
                Loadout(expectedAttackRoll = 8856)
                    .withAccurateStyle()
                    .withRangingPotion()
                    .withRangedBonus(8),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 9504)
                    .withRapidStyle()
                    .withRangingPotion()
                    .withVoid()
                    .withRangedBonus(8),
                Loadout(expectedAttackRoll = 9720)
                    .withAccurateStyle()
                    .withRangingPotion()
                    .withVoid()
                    .withRangedBonus(8),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 10512)
                    .withRapidStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withRangedBonus(8),
                Loadout(expectedAttackRoll = 10728)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withRangedBonus(8),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 12456)
                    .withRapidStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withRigour()
                    .withRangedBonus(8),
                Loadout(expectedAttackRoll = 12744)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withRigour()
                    .withRangedBonus(8),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 34254)
                    .withRapidStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withRigour()
                    .withRangedBonus(bonus = 134),
                Loadout(expectedAttackRoll = 35046)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withRigour()
                    .withRangedBonus(bonus = 134),
                /* Loadout Group */
                Loadout(expectedAttackRoll = 47084)
                    .withRapidStyle()
                    .withSmellingSalts()
                    .withRigour()
                    .withRangedBonus(bonus = 234),
                Loadout(expectedAttackRoll = 47978)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withRigour()
                    .withRangedBonus(bonus = 234),
            )
    }
}
