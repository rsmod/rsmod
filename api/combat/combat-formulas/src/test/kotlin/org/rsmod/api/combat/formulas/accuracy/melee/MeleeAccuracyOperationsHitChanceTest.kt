package org.rsmod.api.combat.formulas.accuracy.melee

import java.util.EnumSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class MeleeAccuracyOperationsHitChanceTest {
    @TestWithArgs(RollsProvider::class)
    fun `calculate hit chance based on rolls`(rolls: Rolls) {
        val attackRoll = rolls.attackRoll
        val defenceRoll = rolls.defenceRoll
        val expectedAccuracy = rolls.expectedAccuracy
        // Attributes are only used for "modifyHitChance" in this test, as such the only
        // attribute that currently affects the results is osmumten's fang.
        val meleeAttributes = rolls.meleeAttributes
        val npcAttributes = rolls.npcAttributes

        val hitChance = MeleeAccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
        val modified =
            MeleeAccuracyOperations.modifyHitChance(
                hitChance = hitChance,
                attackRoll = attackRoll,
                defenceRoll = defenceRoll,
                meleeAttributes = meleeAttributes,
                npcAttributes = npcAttributes,
            )
        assertEquals(expectedAccuracy, modified / 100.0)
    }

    data class Rolls(
        val attackRoll: Int,
        val defenceRoll: Int,
        val expectedAccuracy: Double,
        val meleeAttributes: EnumSet<MeleeAttr> = EnumSet.noneOf(MeleeAttr::class.java),
        val npcAttributes: EnumSet<NpcAttr> = EnumSet.noneOf(NpcAttr::class.java),
    ) {
        fun withMeleeAttributes(vararg attributes: MeleeAttr) =
            copy(meleeAttributes = EnumSet.copyOf(attributes.toSet()))

        fun withNpcAttributes(vararg attributes: NpcAttr) =
            copy(npcAttributes = EnumSet.copyOf(attributes.toSet()))
    }

    private object RollsProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                /* Real scenario test rolls */
                Rolls(attackRoll = -153, defenceRoll = 20988, expectedAccuracy = 0.0),
                Rolls(attackRoll = -153, defenceRoll = -360, expectedAccuracy = 78.69),
                Rolls(attackRoll = 768, defenceRoll = -360, expectedAccuracy = 100.0),
                Rolls(attackRoll = 14278, defenceRoll = 8008, expectedAccuracy = 71.95),
                Rolls(attackRoll = 18054, defenceRoll = 5760, expectedAccuracy = 84.04),
                Rolls(attackRoll = 13068, defenceRoll = 24192, expectedAccuracy = 27.01),
                Rolls(attackRoll = 18590, defenceRoll = 11036, expectedAccuracy = 91.19)
                    .withMeleeAttributes(MeleeAttr.Stab, MeleeAttr.OsmumtensFang)
                    .withNpcAttributes(NpcAttr.Amascut),
                Rolls(attackRoll = 18590, defenceRoll = 26936, expectedAccuracy = 46.01)
                    .withMeleeAttributes(MeleeAttr.Stab, MeleeAttr.OsmumtensFang),
                Rolls(attackRoll = 46207, defenceRoll = 5760, expectedAccuracy = 99.48)
                    .withMeleeAttributes(MeleeAttr.Stab, MeleeAttr.OsmumtensFang),
                Rolls(attackRoll = 46207, defenceRoll = 340, expectedAccuracy = 100.0)
                    .withMeleeAttributes(MeleeAttr.Stab, MeleeAttr.OsmumtensFang),
                /* Boundary test rolls */
                Rolls(
                    attackRoll = Int.MAX_VALUE,
                    defenceRoll = Int.MIN_VALUE,
                    expectedAccuracy = 100.0,
                ),
                Rolls(
                    attackRoll = Int.MIN_VALUE,
                    defenceRoll = Int.MAX_VALUE,
                    expectedAccuracy = 0.0,
                ),
                Rolls(attackRoll = Int.MAX_VALUE, defenceRoll = 0, expectedAccuracy = 100.0),
                Rolls(attackRoll = Int.MIN_VALUE, defenceRoll = 0, expectedAccuracy = 0.0),
                Rolls(attackRoll = 0, defenceRoll = Int.MAX_VALUE, expectedAccuracy = 0.0),
                Rolls(attackRoll = 0, defenceRoll = Int.MIN_VALUE, expectedAccuracy = 100.0),
            )
        }
    }
}
