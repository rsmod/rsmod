package org.rsmod.api.combat.maxhit.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class PlayerRangedMaxHitTest {
    /**
     * Verifies that the `Sharp eye` prayer correctly applies its `+1` range boost when the player's
     * visible range level is `20` or lower.
     *
     * Previously, rounding behavior caused the prayer to have no effect in these cases. This test
     * ensures the fix is properly applied.
     */
    @Test
    fun `ensure sharp eye prayer bonus is not ignored for low ranged levels`() {
        val visibleRangedLvl = 10
        val styleBonus = 11
        val prayerBonus = 1.05
        val rangeStrBonus = 42

        val effectiveRanged =
            PlayerRangedMaxHit.calculateEffectiveRanged(
                visibleRangedLvl = visibleRangedLvl,
                styleBonus = styleBonus,
                prayerBonus = prayerBonus,
                voidBonus = 1.0,
            )
        val baseDamage = PlayerRangedMaxHit.calculateBaseDamage(effectiveRanged, rangeStrBonus)

        // This scenario would return `3` pre-fix.
        assertEquals(4, baseDamage)
    }

    @TestWithArgs(LoadoutProvider::class)
    fun `calculate base damage with loadout`(loadout: Loadout) {
        val expectedMaxHit = loadout.expectedMaxHit
        val visibleRangedLvl = loadout.visibleRangedLvl
        val styleBonus = loadout.styleBonus
        val prayerBonus = loadout.prayerBonus
        val voidBonus = loadout.voidBonus
        val rangedStrBonus = loadout.rangedStrBonus

        val effectiveRanged =
            PlayerRangedMaxHit.calculateEffectiveRanged(
                visibleRangedLvl = visibleRangedLvl,
                styleBonus = styleBonus,
                prayerBonus = prayerBonus,
                voidBonus = voidBonus,
            )
        val baseDamage = PlayerRangedMaxHit.calculateBaseDamage(effectiveRanged, rangedStrBonus)

        assertEquals(expectedMaxHit, baseDamage)
    }

    data class Loadout(
        val expectedMaxHit: Int,
        val visibleRangedLvl: Int = 99,
        val styleBonus: Int = 8,
        val prayerBonus: Double = 1.0,
        val voidBonus: Double = 1.0,
        val rangedStrBonus: Int = 0,
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

        fun withEliteVoid() = copy(voidBonus = 1.125)

        fun withAccurateStyle() = copy(styleBonus = 11)

        fun withOtherStyle() = copy(styleBonus = 8)

        fun withRangedStrBonus(bonus: Int) = copy(rangedStrBonus = bonus)

        fun withRigour() = copy(prayerBonus = 1.23)
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            testArgsOfSingleParam(
                /* Loadout Group */
                Loadout(expectedMaxHit = 12).withOtherStyle().withRangedStrBonus(bonus = 7),
                Loadout(expectedMaxHit = 12).withAccurateStyle().withRangedStrBonus(bonus = 7),
                /* Loadout Group */
                Loadout(expectedMaxHit = 13)
                    .withOtherStyle()
                    .withVoid()
                    .withRangedStrBonus(bonus = 7),
                Loadout(expectedMaxHit = 13)
                    .withAccurateStyle()
                    .withVoid()
                    .withRangedStrBonus(bonus = 7),
                /* Loadout Group */
                Loadout(expectedMaxHit = 13)
                    .withOtherStyle()
                    .withEliteVoid()
                    .withRangedStrBonus(bonus = 7),
                Loadout(expectedMaxHit = 14)
                    .withAccurateStyle()
                    .withEliteVoid()
                    .withRangedStrBonus(bonus = 7),
                /* Loadout Group */
                Loadout(expectedMaxHit = 13)
                    .withOtherStyle()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 7),
                Loadout(expectedMaxHit = 14)
                    .withAccurateStyle()
                    .withRangingPotion()
                    .withRangedStrBonus(bonus = 7),
                /* Loadout Group */
                Loadout(expectedMaxHit = 15)
                    .withOtherStyle()
                    .withRangingPotion()
                    .withVoid()
                    .withRangedStrBonus(bonus = 7),
                Loadout(expectedMaxHit = 15)
                    .withAccurateStyle()
                    .withRangingPotion()
                    .withVoid()
                    .withRangedStrBonus(bonus = 7),
                /* Loadout Group */
                Loadout(expectedMaxHit = 18)
                    .withOtherStyle()
                    .withRangingPotion()
                    .withVoid()
                    .withRigour()
                    .withRangedStrBonus(bonus = 7),
                Loadout(expectedMaxHit = 18)
                    .withAccurateStyle()
                    .withRangingPotion()
                    .withVoid()
                    .withRigour()
                    .withRangedStrBonus(bonus = 7),
                /* Loadout Group */
                Loadout(expectedMaxHit = 52)
                    .withOtherStyle()
                    .withSmellingSalts()
                    .withRigour()
                    .withRangedStrBonus(bonus = 143),
                Loadout(expectedMaxHit = 53)
                    .withAccurateStyle()
                    .withSmellingSalts()
                    .withRigour()
                    .withRangedStrBonus(bonus = 143),
            )
    }
}
