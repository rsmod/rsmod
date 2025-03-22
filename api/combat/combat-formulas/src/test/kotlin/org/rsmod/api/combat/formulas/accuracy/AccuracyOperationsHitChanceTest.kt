package org.rsmod.api.combat.formulas.accuracy

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class AccuracyOperationsHitChanceTest {
    @TestWithArgs(RollsProvider::class)
    fun `calculate hit chance based on rolls`(rolls: Rolls) {
        val (attackRoll, defenceRoll, expectedAccuracy) = rolls
        val hitChance = AccuracyOperations.calculateHitChance(attackRoll, defenceRoll)
        assertEquals(expectedAccuracy, hitChance / 100.0)
    }

    data class Rolls(val attackRoll: Int, val defenceRoll: Int, val expectedAccuracy: Double)

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
