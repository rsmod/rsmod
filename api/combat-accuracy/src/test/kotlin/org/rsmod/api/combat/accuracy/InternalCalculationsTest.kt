package org.rsmod.api.combat.accuracy

import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.testing.params.testArgsOfSingleParam

class InternalCalculationsTest {
    @TestWithArgs(DecimalProvider::class)
    fun `ensure safePercentScale handles floating point precision errors`(scaled: ScaledValue) {
        val percent = safePercentScale(scaled.value)
        assertEquals(scaled.expectedPercent, percent)
    }

    data class ScaledValue(val value: Double, val expectedPercent: Int)

    private object DecimalProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return testArgsOfSingleParam(
                ScaledValue(1.0, 100),
                ScaledValue(1.01, 101),
                ScaledValue(1.05, 105),
                ScaledValue(1.10, 110),
                ScaledValue(1.14, 114),
                ScaledValue(1.15, 115),
                ScaledValue(1.16, 116),
                ScaledValue(1.19, 119),
                ScaledValue(1.20, 120),
                ScaledValue(1.25, 125),
                ScaledValue(1.50, 150),
                ScaledValue(1.75, 175),
                ScaledValue(1.99, 199),
                ScaledValue(2.00, 200),
                ScaledValue(2.50, 250),
                ScaledValue(3.00, 300),
                ScaledValue(10.00, 1000),
            )
        }
    }
}
