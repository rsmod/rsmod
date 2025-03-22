package org.rsmod.api.combat.formulas.accuracy.melee

import java.util.EnumSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.combat.formulas.accuracy.AccuracyOperations
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

        val hitChance = AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
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
                Rolls(attackRoll = 18590, defenceRoll = 11036, expectedAccuracy = 91.19)
                    .withMeleeAttributes(MeleeAttr.Stab, MeleeAttr.OsmumtensFang)
                    .withNpcAttributes(NpcAttr.Amascut),
                Rolls(attackRoll = 18590, defenceRoll = 26936, expectedAccuracy = 46.01)
                    .withMeleeAttributes(MeleeAttr.Stab, MeleeAttr.OsmumtensFang),
                Rolls(attackRoll = 46207, defenceRoll = 5760, expectedAccuracy = 99.48)
                    .withMeleeAttributes(MeleeAttr.Stab, MeleeAttr.OsmumtensFang),
                Rolls(attackRoll = 46207, defenceRoll = 340, expectedAccuracy = 100.0)
                    .withMeleeAttributes(MeleeAttr.Stab, MeleeAttr.OsmumtensFang),
            )
        }
    }
}
