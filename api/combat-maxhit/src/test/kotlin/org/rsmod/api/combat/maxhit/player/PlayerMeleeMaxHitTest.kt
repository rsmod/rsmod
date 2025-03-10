package org.rsmod.api.combat.maxhit.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class PlayerMeleeMaxHitTest {
    /**
     * Verifies that the `Burst of Strength` prayer correctly applies its `+1` strength boost when
     * the player's visible strength level is `20` or lower.
     *
     * Previously, rounding behavior caused the prayer to have no effect in these cases. This test
     * ensures the fix is properly applied.
     */
    @Test
    fun `ensure burst of strength prayer bonus is not ignored for low strength levels`() {
        val visibleStrengthLvl = 10
        val styleBonus = 11
        val prayerBonus = 1.05
        val strengthBonus = 42

        val effectiveStrength =
            PlayerMeleeMaxHit.calculateEffectiveStrength(
                visibleStrengthLvl = visibleStrengthLvl,
                styleBonus = styleBonus,
                prayerBonus = prayerBonus,
                voidBonus = 1.0,
                weaponBonus = 1.0,
            )
        val baseDamage = PlayerMeleeMaxHit.calculateBaseDamage(effectiveStrength, strengthBonus)

        // This scenario would return `3` pre-fix.
        assertEquals(4, baseDamage)
    }

    @TestWithArgs(LoadoutProvider::class)
    fun `calculate base damage with loadout`(loadout: Loadout) {
        val expectedMaxHit = loadout.expectedMaxHit
        val visibleStrengthLvl = loadout.visibleStrengthLvl
        val styleBonus = loadout.styleBonus
        val prayerBonus = loadout.prayerBonus
        val voidBonus = loadout.voidBonus
        val weaponBonus = loadout.weaponBonus
        val strengthBonus = loadout.strengthBonus

        val effectiveStrength =
            PlayerMeleeMaxHit.calculateEffectiveStrength(
                visibleStrengthLvl = visibleStrengthLvl,
                styleBonus = styleBonus,
                prayerBonus = prayerBonus,
                voidBonus = voidBonus,
                weaponBonus = weaponBonus,
            )
        val baseDamage = PlayerMeleeMaxHit.calculateBaseDamage(effectiveStrength, strengthBonus)

        assertEquals(expectedMaxHit, baseDamage)
    }

    data class Loadout(
        val expectedMaxHit: Int,
        val visibleStrengthLvl: Int = 99,
        val styleBonus: Int = 8,
        val prayerBonus: Double = 1.0,
        val voidBonus: Double = 1.0,
        val weaponBonus: Double = 1.0,
        val strengthBonus: Int = 0,
    ) {
        fun withSuperStrPotion(): Loadout {
            val add = 5 + (visibleStrengthLvl * 0.15).toInt()
            return copy(visibleStrengthLvl = visibleStrengthLvl + add)
        }

        fun withSmellingSalts(): Loadout {
            val add = 11 + (visibleStrengthLvl * 0.16).toInt()
            return copy(visibleStrengthLvl = visibleStrengthLvl + add)
        }

        fun withVoid() = copy(voidBonus = 1.1)

        fun withAggressiveStyle() = copy(styleBonus = 11)

        fun withControlledStyle() = copy(styleBonus = 9)

        fun withDefensiveStyle() = copy(styleBonus = 8)

        fun withStrengthBonus(bonus: Int) = copy(strengthBonus = bonus)

        fun withBurstOfStrength() = copy(prayerBonus = 1.05)

        fun withSuperhumanStrength() = copy(prayerBonus = 1.1)

        fun withPiety() = copy(prayerBonus = 1.23)

        fun withSoulStacks(stacks: Int) = copy(weaponBonus = 1.0 + (stacks * 0.06))
    }

    private object LoadoutProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            testArgsOfSingleParam(
                /* Loadout Group */
                Loadout(expectedMaxHit = 11).withDefensiveStyle(),
                Loadout(expectedMaxHit = 11).withControlledStyle(),
                Loadout(expectedMaxHit = 11).withAggressiveStyle(),
                /* Loadout Group */
                Loadout(expectedMaxHit = 12).withDefensiveStyle().withVoid(),
                Loadout(expectedMaxHit = 12).withControlledStyle().withVoid(),
                Loadout(expectedMaxHit = 12).withAggressiveStyle().withVoid(),
                /* Loadout Group */
                Loadout(expectedMaxHit = 13).withDefensiveStyle().withSuperStrPotion(),
                Loadout(expectedMaxHit = 13).withControlledStyle().withSuperStrPotion(),
                Loadout(expectedMaxHit = 13).withAggressiveStyle().withSuperStrPotion(),
                /* Loadout Group */
                Loadout(expectedMaxHit = 14).withDefensiveStyle().withSuperStrPotion().withVoid(),
                Loadout(expectedMaxHit = 14).withControlledStyle().withSuperStrPotion().withVoid(),
                Loadout(expectedMaxHit = 14).withAggressiveStyle().withSuperStrPotion().withVoid(),
                /* Loadout Group */
                Loadout(expectedMaxHit = 15).withDefensiveStyle().withSmellingSalts().withVoid(),
                Loadout(expectedMaxHit = 15).withControlledStyle().withSmellingSalts().withVoid(),
                Loadout(expectedMaxHit = 15).withAggressiveStyle().withSmellingSalts().withVoid(),
                /* Loadout Group */
                Loadout(expectedMaxHit = 18)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety(),
                Loadout(expectedMaxHit = 18)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety(),
                Loadout(expectedMaxHit = 18)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety(),
                /* Loadout Group */
                Loadout(expectedMaxHit = 51)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 0)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 51)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 0)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 52)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 0)
                    .withStrengthBonus(bonus = 121),
                /* Loadout Group */
                Loadout(expectedMaxHit = 53)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 1)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 53)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 1)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 54)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 1)
                    .withStrengthBonus(bonus = 121),
                /* Loadout Group */
                Loadout(expectedMaxHit = 56)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 2)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 56)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 2)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 57)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 2)
                    .withStrengthBonus(bonus = 121),
                /* Loadout Group */
                Loadout(expectedMaxHit = 58)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 3)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 58)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 3)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 59)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 3)
                    .withStrengthBonus(bonus = 121),
                /* Loadout Group */
                Loadout(expectedMaxHit = 61)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 4)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 61)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 4)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 62)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 4)
                    .withStrengthBonus(bonus = 121),
                /* Loadout Group */
                Loadout(expectedMaxHit = 63)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 5)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 63)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 5)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 64)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withPiety()
                    .withSoulStacks(stacks = 5)
                    .withStrengthBonus(bonus = 121),
                /* Loadout Group */
                Loadout(expectedMaxHit = 56)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withBurstOfStrength()
                    .withSoulStacks(stacks = 5)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 56)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withBurstOfStrength()
                    .withSoulStacks(stacks = 5)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 57)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withVoid()
                    .withBurstOfStrength()
                    .withSoulStacks(stacks = 5)
                    .withStrengthBonus(bonus = 121),
                /* Loadout Group */
                Loadout(expectedMaxHit = 53)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withSuperhumanStrength()
                    .withSoulStacks(stacks = 5)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 53)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withSuperhumanStrength()
                    .withSoulStacks(stacks = 5)
                    .withStrengthBonus(bonus = 121),
                Loadout(expectedMaxHit = 53)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withSuperhumanStrength()
                    .withSoulStacks(stacks = 5)
                    .withStrengthBonus(bonus = 121),
                /* Loadout Group */
                Loadout(expectedMaxHit = 67)
                    .withDefensiveStyle()
                    .withSmellingSalts()
                    .withPiety()
                    .withStrengthBonus(bonus = 201),
                Loadout(expectedMaxHit = 67)
                    .withControlledStyle()
                    .withSmellingSalts()
                    .withPiety()
                    .withStrengthBonus(bonus = 201),
                Loadout(expectedMaxHit = 68)
                    .withAggressiveStyle()
                    .withSmellingSalts()
                    .withPiety()
                    .withStrengthBonus(bonus = 201),
            )
    }
}
