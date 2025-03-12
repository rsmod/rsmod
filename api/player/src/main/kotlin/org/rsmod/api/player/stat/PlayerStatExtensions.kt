package org.rsmod.api.player.stat

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import org.rsmod.api.player.output.UpdateStat
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatType

/** Returns the **current**, **visible** level for [stat]. */
public fun Player.stat(stat: StatType): Int {
    return statMap.getCurrentLevel(stat).toInt()
}

/** Returns the **base** level for [stat], based on its xp without any boosts. */
public fun Player.statBase(stat: StatType): Int {
    return statMap.getBaseLevel(stat).toInt()
}

/**
 * Restores the current level of [stat] to its base level.
 *
 * **Notes:**
 * - This function resets the current level to the base level, whether it is above or below it.
 * - If the current level is already equal to the base level, this function does nothing.
 */
public fun Player.statRestore(stat: StatType, invisibleLevels: InvisibleLevels) {
    val currLevel = stat(stat)
    val baseLevel = statBase(stat)
    val delta = baseLevel - currLevel
    when {
        delta == 0 -> return
        delta < 0 -> statSub(stat, delta.absoluteValue, percent = 0, invisibleLevels)
        else -> statAdd(stat, delta, percent = 0, invisibleLevels)
    }
}

/**
 * Calls [statRestore] for every [StatType] in [stats].
 *
 * @see [statRestore]
 */
public fun Player.statRestoreAll(stats: Iterable<StatType>, invisibleLevels: InvisibleLevels) {
    for (stat in stats) {
        statRestore(stat, invisibleLevels)
    }
}

public fun Player.statAdvance(
    stat: StatType,
    xp: Double,
    eventBus: EventBus,
    invisibleLevels: InvisibleLevels,
    rate: Double = xpRate,
): Int {
    val startLevel = statMap.getBaseLevel(stat)
    val addedXp = PlayerSkillXP.internalAddXP(this, stat, xp, rate, eventBus, invisibleLevels)
    val endLevel = statMap.getBaseLevel(stat)
    if (startLevel != endLevel) {
        // TODO: Engine queue for changestat
    }
    return addedXp
}

/**
 * #### Warning
 * Increases the player's stat level based on their **current** level. Use [statBoost] if you wish
 * to increase levels based on the **base** level instead.
 *
 * Note: This function ensures that the player's stat level does not exceed `255`.
 *
 * @throws IllegalArgumentException if [constant] is negative (use `statSub` instead), or if
 *   [percent] is not within the range `0..100`.
 */
public fun Player.statAdd(
    stat: StatType,
    constant: Int,
    percent: Int,
    invisibleLevels: InvisibleLevels,
) {
    require(constant >= 0) { "Constant `$constant` must be positive. Use `statSub` instead." }
    require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

    val current = statMap.getCurrentLevel(stat).toInt()
    val calculated = current + (constant + (current * percent) / 100)
    val cappedLevel = min(255, calculated)

    statMap.setCurrentLevel(stat, cappedLevel.toByte())
    updateStat(stat, invisibleLevels)

    if (cappedLevel != current) {
        // TODO: Engine queue for changestat
    }
}

/**
 * Increases the player's stat level based on their **base** level.
 *
 * Note: This function ensures that the player's stat level does not exceed `255`.
 *
 * @throws IllegalArgumentException if [constant] is negative (use `statDrain` if required), or if
 *   [percent] is not within range of `0` to `100`.
 */
public fun Player.statBoost(
    stat: StatType,
    constant: Int,
    percent: Int,
    invisibleLevels: InvisibleLevels,
) {
    require(constant >= 0) { "Constant `$constant` must be positive. Use `statDrain` instead." }
    require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

    val base = statMap.getBaseLevel(stat).toInt()
    val boost = constant + (base * percent) / 100

    val current = statMap.getCurrentLevel(stat).toInt()
    val cappedBoost = min(base + boost, current + boost) - current

    statAdd(stat, cappedBoost, 0, invisibleLevels)
}

/**
 * #### Warning
 * Decreases the player's stat level based on their **current** level. Use [statDrain] if you wish
 * to decrease levels based on the **base** level instead.
 *
 * Note: This function ensures that the player's stat level does not fall below `0`.
 *
 * @throws IllegalArgumentException if [constant] is negative, or if [percent] is not within the
 *   range `0..100`.
 */
public fun Player.statSub(
    stat: StatType,
    constant: Int,
    percent: Int,
    invisibleLevels: InvisibleLevels,
) {
    require(constant >= 0) { "Constant `$constant` must be positive." }
    require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

    val current = statMap.getCurrentLevel(stat).toInt()
    val calculated = current - (constant + (current * percent) / 100)
    val cappedLevel = max(0, calculated)

    statMap.setCurrentLevel(stat, cappedLevel.toByte())
    updateStat(stat, invisibleLevels)

    if (cappedLevel != current) {
        // TODO: Engine queue for changestat
    }
}

/**
 * Decreases the player's stat level based on their **base** level.
 *
 * Note: This function ensures that the player's stat level does not fall below `0`.
 *
 * @throws IllegalArgumentException if [constant] is negative (use `statAdd` if required), or if
 *   [percent] is not within range of `0` to `100`.
 */
public fun Player.statDrain(
    stat: StatType,
    constant: Int,
    percent: Int,
    invisibleLevels: InvisibleLevels,
) {
    require(constant >= 0) { "Constant `$constant` must be positive." }
    require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

    val base = statMap.getBaseLevel(stat).toInt()
    val drain = constant + (base * percent) / 100

    val current = statMap.getCurrentLevel(stat).toInt()
    val cappedDrain = current - min(base - drain, current - drain)

    statSub(stat, cappedDrain, 0, invisibleLevels)
}

/**
 * Restores the player's stat level towards their **base** level.
 *
 * This function increases the player's stat level by a combination of a constant value and a
 * percentage of their **current** level. The restored level will never exceed the player's base
 * level and will not decrease their current level.
 *
 * Note: This function is commonly used to recover from temporary stat reductions or provide partial
 * stat restoration.
 *
 * #### Example
 * If a player's base level for a stat is `99` and their current level is `80`, calling
 * `statHeal(stat, constant = 10, percent = 20)` will restore the stat by `10 + (80 * 20%) = 26`,
 * but it will be capped at the base level of `99`.
 *
 * @throws IllegalArgumentException if [constant] is negative, or if [percent] is not within the
 *   range `0..100`.
 */
public fun Player.statHeal(
    stat: StatType,
    constant: Int,
    percent: Int,
    invisibleLevels: InvisibleLevels,
) {
    require(constant >= 0) { "Constant `$constant` must be positive." }
    require(percent in 0..100) { "Percent must be an integer from 0-100. (0%-100%)" }

    val base = statMap.getBaseLevel(stat).toInt()
    val current = statMap.getCurrentLevel(stat).toInt()
    val calculated = current + (constant + (current * percent) / 100)
    val cappedLevel = calculated.coerceIn(current, base)

    statMap.setCurrentLevel(stat, cappedLevel.toByte())
    updateStat(stat, invisibleLevels)

    if (cappedLevel != current) {
        // TODO: Engine queue for changestat
    }
}

internal fun Player.updateStat(stat: StatType, invisibleLevels: InvisibleLevels) {
    val currXp = statMap.getXP(stat)
    val currLvl = statMap.getCurrentLevel(stat).toInt()
    val hiddenLevel = currLvl + invisibleLevels.get(this, stat)
    UpdateStat.update(this, stat, currXp, currLvl, hiddenLevel)
}
