package org.runerealm.api.player

import kotlin.math.max
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.timers
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.vars.enabledPrayers
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.player.vars.prayerDrainCounter
import org.runerealm.api.player.vars.usingQuickPrayers
import org.runerealm.game.entity.Player
import org.runerealm.game.type.varbit.VarBitType

private var Player.protectPrayersDisabledEnd by intVarp(varps.protect_prayers_disabled_end)
private var Player.protectFromMagic by intVarBit(varbits.protect_from_magic)
private var Player.protectFromMissiles by intVarBit(varbits.protect_from_missiles)
private var Player.protectFromMelee by intVarBit(varbits.protect_from_melee)

/**
 * Deactivates the player's protection prayers (Protect from Magic, Missiles and Melee) and blocks
 * them from being re-activated for [cycles] game cycles. Other active prayers are left untouched.
 *
 * If protection prayers are already disabled, the block is only extended, never shortened.
 *
 * _Note: The prayer tab is responsible for respecting the block; see [isProtectionPrayerDisabled]._
 */
public fun Player.disableProtectionPrayers(cycles: Int) {
    require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
    protectPrayersDisabledEnd = max(protectPrayersDisabledEnd, currentMapClock + cycles)

    protectFromMagic = 0
    protectFromMissiles = 0
    protectFromMelee = 0

    if (appearance.overheadIcon in PROTECTION_OVERHEADS) {
        appearance.overheadIcon = null
    }

    // Mirrors the prayer tab's behavior when the last active prayer is turned off.
    if (enabledPrayers == 0) {
        usingQuickPrayers = false
        prayerDrainCounter = 0
        clearSoftTimer(timers.prayer_drain)
    }
}

/** @return `true` if protection prayers are currently blocked by [disableProtectionPrayers]. */
public fun Player.isProtectionPrayerDisabled(): Boolean {
    val end = protectPrayersDisabledEnd
    // `0` means "never set"; a player that has not been processed yet has a map clock of `-1`.
    return end != 0 && currentMapClock < end
}

/**
 * @return `true` if [prayer] is the `enabled` varbit of a protection prayer affected by
 *   [disableProtectionPrayers].
 */
public fun isProtectionPrayer(prayer: VarBitType): Boolean =
    prayer.isType(varbits.protect_from_magic) ||
        prayer.isType(varbits.protect_from_missiles) ||
        prayer.isType(varbits.protect_from_melee)

private val PROTECTION_OVERHEADS =
    setOf(
        constants.overhead_protect_from_melee,
        constants.overhead_protect_from_missiles,
        constants.overhead_protect_from_magic,
    )
