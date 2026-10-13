package org.runerealm.api.freeze

import kotlin.math.abs
import kotlin.math.max
import org.runerealm.api.config.refs.params
import org.runerealm.api.config.refs.timers
import org.runerealm.api.config.refs.varns
import org.runerealm.api.config.refs.varps
import org.runerealm.api.config.refs.walktriggers
import org.runerealm.api.npc.vars.intVarn
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.player.vars.typePlayerUidVarp
import org.runerealm.api.random.GameRandom
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList
import org.runerealm.game.type.walktrig.WalkTriggerType
import org.runerealm.game.type.walktrig.isType

/*
 * Freeze ("bind") mechanics, following the OSRS Wiki:
 * - A frozen entity cannot use regular movement for the duration of the freeze.
 * - After a freeze ends, the entity is immune to further freezes for 5 cycles (3 seconds). A
 *   frozen entity cannot be frozen again until that immunity runs out.
 * - A freeze applied by a player to another player only holds while both players stay within 10
 *   tiles of each other (x/z only; the level is ignored). Leaving that range ends the freeze.
 * - Npcs resist freezes based on their `freeze_resistance` param: a percentage chance to resist,
 *   where `100` means the npc can never be frozen.
 *
 * Movement is blocked through the `frozen` and `pvp_frozen` walk triggers, which are handled by
 * `FreezeScript`.
 */

/** The number of cycles an entity is immune to freezes after a freeze ends. */
public const val FREEZE_IMMUNITY_CYCLES: Int = 5

/**
 * The maximum distance between a frozen player and the player that froze them. Beyond this range
 * the freeze ends.
 */
public const val PVP_FREEZE_MAX_DISTANCE: Int = 10

private var Player.freezeEnd by intVarp(varps.freeze_end)
private var Player.freezeImmunityEnd by intVarp(varps.freeze_immunity_end)
private var Player.freezeSource by typePlayerUidVarp(varps.freeze_source)

private var Npc.freezeEnd by intVarn(varns.freeze_end)
private var Npc.freezeImmunityEnd by intVarn(varns.freeze_immunity_end)

/** @return `true` if the entity is currently frozen and unable to move. */
public fun PathingEntity.isFrozen(): Boolean = isBefore(freezeEndClock)

/**
 * @return `true` if the entity cannot currently be frozen: either because it is already frozen, or
 *   because its post-freeze immunity has not yet run out.
 */
public fun PathingEntity.isFreezeImmune(): Boolean = isBefore(freezeImmunityEndClock)

/** @return the number of cycles until the entity's freeze ends, or `0` if it is not frozen. */
public fun PathingEntity.remainingFreezeCycles(): Int =
    if (isFrozen()) freezeEndClock - currentMapClock else 0

/*
 * A clock value of `0` means "never set". The check matters because an entity that has not been
 * processed yet has a `currentMapClock` of `-1`, which would otherwise count as before `0`.
 */
private fun PathingEntity.isBefore(clock: Int): Boolean = clock != 0 && currentMapClock < clock

/**
 * Attempts to freeze this entity for [cycles] game cycles, after which it is immune to freezes for
 * [immunityCycles] cycles.
 *
 * The freeze is rejected if the entity is already frozen or still freeze-immune, if it is an npc
 * that resists the freeze (see [Npc.freeze]), or if its current walk trigger cannot be overwritten.
 *
 * @param random only used to roll an npc's freeze resistance.
 * @param source the player that applied the freeze, if any. A freeze applied by a player to another
 *   player ends early once they are more than [PVP_FREEZE_MAX_DISTANCE] tiles apart.
 * @return `true` if the freeze was applied.
 */
public fun PathingEntity.freeze(
    cycles: Int,
    random: GameRandom,
    source: Player? = null,
    immunityCycles: Int = FREEZE_IMMUNITY_CYCLES,
): Boolean =
    when (this) {
        is Player -> freeze(cycles, source, immunityCycles)
        is Npc -> freeze(cycles, random, immunityCycles)
    }

/**
 * Attempts to freeze this player for [cycles] game cycles.
 *
 * @param source the player that applied the freeze. When set, the freeze ends early once the two
 *   players are more than [PVP_FREEZE_MAX_DISTANCE] tiles apart.
 * @return `true` if the freeze was applied.
 * @see [PathingEntity.freeze]
 */
public fun Player.freeze(
    cycles: Int,
    source: Player? = null,
    immunityCycles: Int = FREEZE_IMMUNITY_CYCLES,
): Boolean {
    require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
    require(immunityCycles >= 0) { "`immunityCycles` must not be negative. ($immunityCycles)" }
    if (isFreezeImmune()) {
        return false
    }
    val trigger = if (source != null) walktriggers.pvp_frozen else walktriggers.frozen
    if (!setFreezeWalkTrigger(trigger)) {
        return false
    }
    freezeEnd = currentMapClock + cycles
    freezeImmunityEnd = freezeEnd + immunityCycles
    freezeSource = source?.uid
    // Clears the walk trigger once the freeze runs out, so that it does not linger and block other
    // walk triggers (e.g., emotes) until the player next tries to move.
    softTimer(timers.freeze_thaw, cycles)
    return true
}

/**
 * Attempts to freeze this npc for [cycles] game cycles.
 *
 * The npc's `freeze_resistance` param is a percentage chance to resist the freeze; a value of `100`
 * or more makes it immune. No [random] value is consumed when the npc has no resistance.
 *
 * @return `true` if the freeze was applied.
 * @see [PathingEntity.freeze]
 */
public fun Npc.freeze(
    cycles: Int,
    random: GameRandom,
    immunityCycles: Int = FREEZE_IMMUNITY_CYCLES,
): Boolean {
    require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
    require(immunityCycles >= 0) { "`immunityCycles` must not be negative. ($immunityCycles)" }
    if (isFreezeImmune() || resistsFreeze(random)) {
        return false
    }
    if (!setFreezeWalkTrigger(walktriggers.frozen)) {
        return false
    }
    freezeEnd = currentMapClock + cycles
    freezeImmunityEnd = freezeEnd + immunityCycles
    return true
}

/**
 * Ends the entity's freeze immediately, if it is frozen. The usual post-freeze immunity still
 * applies, counted from now.
 */
public fun PathingEntity.endFreeze() {
    if (!isFrozen()) {
        return
    }
    val immunityCycles = freezeImmunityEndClock - freezeEndClock
    setFreezeClocks(end = currentMapClock, immunityEnd = currentMapClock + immunityCycles)
    clearFreezeState()
}

/** Removes any freeze _and_ freeze immunity from the entity, e.g., on death. */
public fun PathingEntity.clearFreeze() {
    setFreezeClocks(end = 0, immunityEnd = 0)
    clearFreezeState()
}

/**
 * @return the player that froze this player, if they were frozen by a player who is still online.
 */
public fun Player.resolveFreezeSource(playerList: PlayerList): Player? =
    freezeSource?.resolve(playerList)

/**
 * @return `true` if [source] is close enough to this player for a freeze they applied to hold. Only
 *   the x and z coordinates are compared, so changing levels does not break the freeze.
 */
public fun Player.isWithinPvpFreezeRange(source: Player): Boolean {
    val distance = max(abs(source.x - x), abs(source.z - z))
    return distance <= PVP_FREEZE_MAX_DISTANCE
}

private fun Npc.resistsFreeze(random: GameRandom): Boolean {
    val resistance = visType.param(params.freeze_resistance)
    return when {
        resistance <= 0 -> false
        resistance >= 100 -> true
        else -> random.of(maxExclusive = 100) < resistance
    }
}

private fun PathingEntity.setFreezeWalkTrigger(trigger: WalkTriggerType): Boolean {
    // A freeze walk trigger can be left over from an earlier freeze that has already run out.
    if (walkTrigger.isFreezeTrigger()) {
        clearWalkTrigger()
    }
    return walkTrigger(trigger)
}

private fun PathingEntity.clearFreezeState() {
    if (walkTrigger.isFreezeTrigger()) {
        clearWalkTrigger()
    }
    if (this is Player) {
        freezeSource = null
        clearSoftTimer(timers.freeze_thaw)
    }
}

internal fun WalkTriggerType?.isFreezeTrigger(): Boolean =
    isType(walktriggers.frozen) || isType(walktriggers.pvp_frozen)

private val PathingEntity.freezeEndClock: Int
    get() =
        when (this) {
            is Player -> freezeEnd
            is Npc -> freezeEnd
        }

private val PathingEntity.freezeImmunityEndClock: Int
    get() =
        when (this) {
            is Player -> freezeImmunityEnd
            is Npc -> freezeImmunityEnd
        }

private fun PathingEntity.setFreezeClocks(end: Int, immunityEnd: Int) {
    when (this) {
        is Player -> {
            freezeEnd = end
            freezeImmunityEnd = immunityEnd
        }
        is Npc -> {
            freezeEnd = end
            freezeImmunityEnd = immunityEnd
        }
    }
}
