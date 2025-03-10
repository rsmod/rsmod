package org.rsmod.api.combat.maxhit

internal const val PERCENT_SCALE: Int = 100

internal fun scaleByPercent(base: Int, multiplier: Int): Int = (base * multiplier) / PERCENT_SCALE
