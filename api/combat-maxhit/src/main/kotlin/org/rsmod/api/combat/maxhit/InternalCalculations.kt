package org.rsmod.api.combat.maxhit

internal const val PERCENT_SCALE: Int = 100

internal fun scaleByPercent(base: Int, multiplier: Int): Int = (base * multiplier) / PERCENT_SCALE

/**
 * Converts a multiplier (e.g., `1.15` for `115`) into a scaled integer percentage while ensuring
 * correct flooring behavior. Due to floating-point precision issues, `(1.15 * 100).toInt()`
 * unexpectedly produces `114` instead of `115`. To prevent this, a small epsilon correction is
 * applied.
 */
internal fun safePercentScale(multiplier: Double): Int =
    ((multiplier * PERCENT_SCALE) + 0.00001).toInt()
