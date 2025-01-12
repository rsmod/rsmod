package org.rsmod.api.utils.skills

import kotlin.math.round
import org.junit.jupiter.api.Assertions.assertEquals
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.api.utils.skills.SkillingSuccessRate.highRate
import org.rsmod.api.utils.skills.SkillingSuccessRate.lowRate
import org.rsmod.api.utils.skills.SkillingSuccessRate.successRate

/**
 * Note: Output values are rounded to match the precision of the input values sourced from the wiki.
 */
class SkillingSuccessRateTest {
    @TestWithArgs(LowRateProvider::class)
    fun `calculate low-scaled value`(low: Int, level: Int, expected: Double) {
        val lowRate = lowRate(low, level, maxLevel = 99).rounded(scale = 1000)
        assertEquals(expected, lowRate)
    }

    @TestWithArgs(HighRateProvider::class)
    fun `calculate high-scaled value`(high: Int, level: Int, expected: Double) {
        val highRate = highRate(high, level, maxLevel = 99).rounded(scale = 1000)
        assertEquals(expected, highRate)
    }

    @TestWithArgs(SuccessRateProvider::class)
    fun `calculate success rate`(low: Int, high: Int, level: Int, expected: Double) {
        val rate = successRate(low, high, level, maxLevel = 99).rounded(scale = 10_000)
        assertEquals(expected, rate)
    }

    private object LowRateProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return listOf(TestArgs(48, 74, 12.245), TestArgs(8, 85, 1.143))
        }
    }

    private object HighRateProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return listOf(TestArgs(90, 74, 67.041), TestArgs(64, 85, 54.857))
        }
    }

    private object SuccessRateProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return listOf(
                TestArgs(8, 64, 85, 0.2227),
                TestArgs(16, 96, 85, 0.3359),
                TestArgs(48, 90, 62, 0.2930),
                TestArgs(48, 90, 74, 0.3125),
                TestArgs(48, 90, 95, 0.3477),
                TestArgs(48, 90, 99, 0.3555),
                TestArgs(64, 272, 1, 0.2539),
                TestArgs(64, 272, 50, 0.6602),
                TestArgs(64, 272, 75, 0.8672),
                TestArgs(64, 272, 90, 0.9922),
                TestArgs(64, 272, 91, 1.0),
            )
        }
    }
}

private fun Double.rounded(scale: Int): Double = round(this * scale) / scale.toDouble()
