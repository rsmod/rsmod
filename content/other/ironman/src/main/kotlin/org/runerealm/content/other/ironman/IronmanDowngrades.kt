package org.runerealm.content.other.ironman

import jakarta.inject.Inject
import java.time.LocalDate
import java.time.ZoneOffset
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.ironman.IronmanMode
import org.runerealm.api.player.ironman.ironmanMode
import org.runerealm.api.player.stat.statBase
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.game.entity.Player
import org.runerealm.game.stat.PlayerSkillXPTable
import org.runerealm.game.type.stat.StatTypeList

/**
 * Ironman mode changes, following the OSRS Wiki ([Ironman
 * Mode](https://oldschool.runescape.wiki/w/Ironman_Mode)):
 * - On Tutorial Island players may switch freely between modes. Players here start in Lumbridge, so
 *   that freedom applies while the account is still [fresh][isFreshAccount].
 * - Afterwards, Adam only allows downgrades: Hardcore or Ultimate to standard Ironman, and any
 *   Ironman to a normal player. A downgrade only takes effect after its warning period (7 days, or
 *   90 days with the longer protection, which requires a total level of 1,000).
 */
class IronmanDowngrades @Inject constructor(private val statTypes: StatTypeList) {
    private val startingHitpointsXp by lazy {
        PlayerSkillXPTable.getFineXPFromLevel(statTypes[stats.hitpoints].minLevel)
    }

    /**
     * An account counts as fresh (the equivalent of still being on Tutorial Island) until it gains
     * experience in any skill.
     */
    fun isFreshAccount(player: Player): Boolean =
        statTypes.values.all { stat ->
            val fineXp = player.statMap.getFineXP(stat)
            if (stat.id == stats.hitpoints.id) fineXp <= startingHitpointsXp else fineXp == 0
        }

    fun totalLevel(player: Player): Int = statTypes.values.sumOf { player.statBase(it) }

    /** Downgrades Adam allows from [from], per the `ironman_setup` client script. */
    fun allowedDowngrades(from: IronmanMode): Set<IronmanMode> =
        when (from) {
            IronmanMode.Standard -> setOf(IronmanMode.None)
            IronmanMode.Ultimate,
            IronmanMode.Hardcore -> setOf(IronmanMode.Standard, IronmanMode.None)
            else -> emptySet()
        }

    /** Sets [mode] immediately, clearing any pending downgrade. */
    fun setMode(player: Player, mode: IronmanMode) {
        player.ironmanMode = mode
        player.hardcoreDead = 0
        player.downgradePermitted = if (mode.isIronman) PROTECTION_SHORT else 0
        cancelDowngrade(player)
    }

    fun scheduleDowngrade(player: Player, target: IronmanMode, today: Int = today()) {
        val days = if (player.downgradePermitted == PROTECTION_SHORT) SHORT_DAYS else LONG_DAYS
        player.downgradeType = if (target == IronmanMode.None) TYPE_TO_NORMAL else TYPE_ONE_STEP
        player.downgradeDate = today + days
    }

    fun cancelDowngrade(player: Player) {
        player.downgradeType = 0
        player.downgradeDate = 0
    }

    fun hasPendingDowngrade(player: Player): Boolean = player.downgradeType != 0

    fun daysRemaining(player: Player, today: Int = today()): Int =
        (player.downgradeDate - today).coerceAtLeast(0)

    /** Applies a pending downgrade once its warning period has passed. */
    fun applyDueDowngrade(player: Player, today: Int = today()): Boolean {
        if (!hasPendingDowngrade(player) || today < player.downgradeDate) {
            return false
        }
        val target =
            when {
                player.downgradeType == TYPE_TO_NORMAL -> IronmanMode.None
                player.ironmanMode == IronmanMode.Standard -> IronmanMode.None
                else -> IronmanMode.Standard
            }
        setMode(player, target)
        return true
    }

    fun setShortProtection(player: Player) {
        player.downgradePermitted = PROTECTION_SHORT
    }

    fun setLongProtection(player: Player) {
        player.downgradePermitted = PROTECTION_LONG
    }

    fun today(): Int = LocalDate.now(ZoneOffset.UTC).toEpochDay().toInt()

    companion object {
        const val SHORT_DAYS: Int = 7
        const val LONG_DAYS: Int = 90
        const val LONG_PROTECTION_TOTAL_LEVEL: Int = 1000

        /** `ironman_downgradepermitted`: the 7-day radio button is selected when this is 1. */
        const val PROTECTION_SHORT: Int = 1
        const val PROTECTION_LONG: Int = 0

        /** `ironman_downgradetype`: 1 steps down one mode, 2 becomes a normal player. */
        const val TYPE_ONE_STEP: Int = 1
        const val TYPE_TO_NORMAL: Int = 2
    }
}

private var Player.hardcoreDead by intVarBit(varbits.ironman_hardcore_dead)
private var Player.downgradeType by intVarBit(varbits.ironman_downgradetype)
private var Player.downgradeDate by intVarBit(varbits.ironman_downgradedate)
private var Player.downgradePermitted by intVarBit(varbits.ironman_downgradepermitted)
