package org.rsmod.api.utils.skills

import kotlin.math.floor

public object SkillingSuccessRate {
    public fun lowRate(low: Int, level: Int, maxLevel: Int): Double {
        return (low * (maxLevel - level)) / (maxLevel - 1.0)
    }

    public fun highRate(high: Int, level: Int, maxLevel: Int): Double {
        return (high * (level - 1)) / (maxLevel - 1.0)
    }

    public fun successRate(low: Int, high: Int, level: Int, maxLevel: Int): Double {
        val lowRate = lowRate(low, level, maxLevel)
        val highRate = highRate(high, level, maxLevel)
        return (1.0 + floor(lowRate + highRate + 0.5)) / 256.0
    }
}
