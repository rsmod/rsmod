package org.rsmod.api.utils.time

import java.time.Duration
import java.time.LocalDate
import java.util.concurrent.TimeUnit

private val startDate = LocalDate.of(2002, 2, 27).atStartOfDay()

/** Returns the number of days elapsed since [startDate]. */
public fun runeday(): Int {
    val now = LocalDate.now().atStartOfDay()
    return Duration.between(startDate, now).toDays().toInt()
}

/**
 * Returns the number of minutes elapsed since the Unix epoch
 *
 * This function is useful for scenarios where prolonged minute-level cooldowns are required.
 *
 * ### Example Usage
 *
 * ```
 * fun homeTeleport() {
 *     if (player.homeTeleCooldownVarp > epochMinute()) {
 *         player.mes("You can't teleport yet.")
 *         return
 *     }
 *     // Teleport sequence here...
 *     player.homeTeleCooldownVarp = epochMinute() + 30
 * }
 * ```
 */
public fun epochMinute(): Int {
    return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()).toInt()
}
