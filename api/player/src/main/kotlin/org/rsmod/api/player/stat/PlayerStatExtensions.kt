package org.rsmod.api.player.stat

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import org.rsmod.annotations.InternalApi
import org.rsmod.api.config.refs.stats
import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatType

/** Returns the **current**, **visible** level for [stat]. */
@OptIn(InternalApi::class)
public fun Player.stat(stat: StatType): Int {
    return statMap.getCurrentLevel(stat).toInt() and 0xFF
}

/** Returns the **base** level for [stat], based on its xp without any boosts. */
@OptIn(InternalApi::class)
public fun Player.statBase(stat: StatType): Int {
    return statMap.getBaseLevel(stat).toInt() and 0xFF
}

/**
 * Restores the current level of [stat] to its base level.
 *
 * **Notes:**
 * - This function resets the current level to the base level, whether it is above or below it.
 * - If the current level is already equal to the base level, this function does nothing.
 */
public fun Player.statRestore(stat: StatType) {
    val currLevel = stat(stat)
    val baseLevel = statBase(stat)
    val delta = baseLevel - currLevel
    when {
        delta == 0 -> return
        delta < 0 -> statSub(stat, delta.absoluteValue, percent = 0)
        else -> statAdd(stat, delta, percent = 0)
    }
}

/**
 * Calls [statRestore] for every [StatType] in [stats].
 *
 * @see [statRestore]
 */
public fun Player.statRestoreAll(stats: Iterable<StatType>) {
    for (stat in stats) {
        statRestore(stat)
    }
}

public fun Player.statAdvance(stat: StatType, xp: Double, rate: Double = xpRate): Int {
    return PlayerSkillXP.internalAddXP(this, stat, xp, rate)
}

/**
 * Increases the player's current [stat] level.
 *
 * **Notes:**
 * - Repeatedly calling this function will continue increasing the stat level, regardless of its
 *   current level, until it reaches `255`.
 * - Use [statBoost] instead if you want the increase to be capped relative to the player's **base**
 *   stat level.
 *
 * #### Hero Points
 * If [stat] is `hitpoints` and the resulting [hitpoints] level is greater than or equal to
 * [baseHitpointsLvl], [Player.heroPoints] is automatically cleared.
 *
 * @param constant The fixed amount to add to the player's current stat level.
 * @param percent The percentage (`0`-`100`) of the player's **base** stat level to add.
 * @throws IllegalArgumentException if [constant] is negative (use `statSub` instead), or if
 *   [percent] is not within the range `0..100`.
 */
@OptIn(InternalApi::class)
public fun Player.statAdd(stat: StatType, constant: Int, percent: Int) {
    require(constant >= 0) { "Constant `$constant` must be positive. Use `statSub` instead." }
    require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

    val base = statBase(stat)
    val current = stat(stat)
    val calculated = current + (constant + (base * percent) / 100)
    val cappedLevel = min(255, calculated)

    statMap.setCurrentLevel(stat, cappedLevel.toByte())
    updateStat(stat)

    val clearHeroPoints = stat.isType(stats.hitpoints) && hitpoints >= baseHitpointsLvl
    if (clearHeroPoints) {
        clearHeroPoints()
    }

    if (cappedLevel != current) {
        changeStat(stat)
    }
}

/**
 * Increases the player's current [stat] level while ensuring it does not exceed a calculated
 * threshold.
 *
 * Unlike [statAdd], this function prevents the stat level from increasing beyond the sum of the
 * player's **base** stat level and the calculated boost.
 *
 * **Notes:**
 * - Repeatedly calling this function will continue increasing the stat level, regardless of its
 *   current level, until it reaches the calculated threshold.
 * - Use [statAdd] instead if you want the stat level to continue increasing without an upper cap.
 *
 * @param constant The fixed amount to add to the player's current stat level.
 * @param percent The percentage (`0`-`100`) of the player's **base** stat level to add.
 * @throws IllegalArgumentException if [constant] is negative (use `statSub` instead), or if
 *   [percent] is not within the range `0..100`.
 */
public fun Player.statBoost(stat: StatType, constant: Int, percent: Int) {
    require(constant >= 0) { "Constant `$constant` must be positive. Use `statDrain` instead." }
    require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

    val base = statBase(stat)
    val boost = constant + (base * percent) / 100

    val current = stat(stat)
    val cappedBoost = min(base + boost, current + boost) - current

    statAdd(stat, cappedBoost, 0)
}

/**
 * Decreases the player's current [stat] level.
 *
 * **Notes:**
 * - Repeatedly calling this function will continue subtracting from the stat level, regardless of
 *   its current level, until it reaches `0`.
 * - Use [statDrain] instead if you want the subtraction to be capped based on the outcome of the
 *   [constant] and [percent] calculation.
 *
 * @param constant The fixed amount to subtract from the player's current stat level.
 * @param percent The percentage (`0`-`100`) of the player's **base** stat level to subtract.
 * @throws IllegalArgumentException if [constant] is negative, or if [percent] is not within the
 *   range `0..100`.
 */
public fun Player.statSub(stat: StatType, constant: Int, percent: Int) {
    require(constant >= 0) { "Constant `$constant` must be positive." }
    require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

    val base = statBase(stat)
    val current = stat(stat)
    val calculated = current - (constant + (base * percent) / 100)
    val cappedLevel = max(0, calculated)

    statMap.setCurrentLevel(stat, cappedLevel.toByte())
    updateStat(stat)

    if (cappedLevel != current) {
        changeStat(stat)
    }
}

/**
 * Decreases the player's current [stat] level while ensuring it does not fall below a calculated
 * threshold.
 *
 * Unlike [statSub], this function prevents the stat level from dropping below the result of the
 * [constant] and [percent] calculation.
 *
 * **Notes:**
 * - This function ensures that the player's stat level does not fall below `0`.
 * - Use [statSub] instead if you want the stat level to keep decreasing with repeated calls, even
 *   if it falls below the calculated threshold.
 *
 * @param constant The fixed amount to subtract from the player's current stat level.
 * @param percent The percentage of the player's **base** stat level to subtract.
 * @throws IllegalArgumentException if [constant] is negative (use `statAdd` if required), or if
 *   [percent] is not within range of `0` to `100`.
 */
public fun Player.statDrain(stat: StatType, constant: Int, percent: Int) {
    require(constant >= 0) { "Constant `$constant` must be positive." }
    require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

    val base = statBase(stat)
    val drain = constant + (base * percent) / 100

    val current = stat(stat)
    val cappedDrain = current - min(base - drain, current - drain)

    statSub(stat, cappedDrain, 0)
}

/**
 * Restores the player's stat level towards their **base** level.
 *
 * This function increases the player's stat level by a combination of a constant value and a
 * percentage of their **base** level. The restored level will never exceed the player's base level
 * and will not decrease their current level.
 *
 * Commonly used to recover from temporary stat reductions or provide partial stat restoration.
 *
 * #### Hero Points
 * If [stat] is `hitpoints` and the resulting [hitpoints] level is greater than or equal to
 * [baseHitpointsLvl], [Player.heroPoints] is automatically cleared.
 *
 * #### Example
 * If a player's base level for a stat is `80` and their current level is `50`, calling
 * `statHeal(stat, constant = 25, percent = 20)` will restore the stat by `25 + (80 * 20%) = 42`,
 * but it will be capped at the base level of `80` as opposed to `92`.
 *
 * @param constant The fixed amount to add to the player's current stat level.
 * @param percent The percentage (`0`-`100`) of the player's **base** stat level to add.
 * @throws IllegalArgumentException if [constant] is negative, or if [percent] is not within the
 *   range `0..100`.
 */
@OptIn(InternalApi::class)
public fun Player.statHeal(stat: StatType, constant: Int, percent: Int) {
    require(constant >= 0) { "Constant `$constant` must be positive." }
    require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

    val base = statBase(stat)
    val current = stat(stat)
    val calculated = current + (constant + (base * percent) / 100)
    val cappedLevel = calculated.coerceIn(current, base)

    statMap.setCurrentLevel(stat, cappedLevel.toByte())
    updateStat(stat)

    val clearHeroPoints = stat.isType(stats.hitpoints) && hitpoints >= baseHitpointsLvl
    if (clearHeroPoints) {
        clearHeroPoints()
    }

    if (cappedLevel != current) {
        changeStat(stat)
    }
}

@OptIn(InternalApi::class) internal fun Player.updateStat(stat: StatType) = markStatUpdate(stat)
