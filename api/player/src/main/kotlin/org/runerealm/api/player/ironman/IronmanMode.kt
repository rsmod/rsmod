package org.runerealm.api.player.ironman

import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.game.entity.Player

/**
 * Ironman game modes, keyed by their `ironman` varbit value (see cache enum `enum_859` for the
 * display names).
 *
 * [chatIcon] is the client player type sent with public chat to draw the mode's chat badge.
 */
public enum class IronmanMode(
    public val varValue: Int,
    public val displayName: String,
    public val chatIcon: Int,
) {
    None(0, "None", constants.mod_clientcode_player),
    Standard(1, "Ironman", 3),
    Ultimate(2, "Ultimate Ironman", 4),
    Hardcore(3, "Hardcore Ironman", 5),
    Group(4, "Group Ironman", 9),
    HardcoreGroup(5, "Hardcore Group Ironman", 10),
    UnrankedGroup(6, "Unranked Group Ironman", 11);

    public val isIronman: Boolean
        get() = this != None

    public companion object {
        public operator fun get(varValue: Int): IronmanMode =
            entries.firstOrNull { it.varValue == varValue } ?: None
    }
}

private var Player.ironmanVar by intVarBit(varbits.ironman)

public var Player.ironmanMode: IronmanMode
    get() = IronmanMode[ironmanVar]
    set(value) {
        ironmanVar = value.varValue
    }

/** `true` for every ironman mode, including Ultimate and Hardcore. */
public val Player.isIronman: Boolean
    get() = ironmanMode.isIronman

public val Player.isUltimateIronman: Boolean
    get() = ironmanMode == IronmanMode.Ultimate

public val Player.isHardcoreIronman: Boolean
    get() = ironmanMode == IronmanMode.Hardcore

/** The chat badge for public chat. Moderator crowns take priority over ironman badges. */
public val Player.chatIcon: Int
    get() =
        if (modLevel.clientCode != constants.mod_clientcode_player) {
            modLevel.clientCode
        } else {
            ironmanMode.chatIcon
        }

private var Player.hardcoreDead by intVarBit(varbits.ironman_hardcore_dead)
private var Player.downgradeType by intVarBit(varbits.ironman_downgradetype)
private var Player.downgradeDate by intVarBit(varbits.ironman_downgradedate)

/** Chat message shown when a Hardcore Ironman dies an unsafe death. (Wiki: Ironman Mode) */
public const val HARDCORE_DEATH_MESSAGE: String =
    "You have fallen as a Hardcore Ironman, your Hardcore status has been revoked."

/** Message for an Ironman trying to take an obj that another player originally owned. */
public const val IRONMAN_TAKE_MESSAGE: String =
    "You're an Ironman, so you can't take items that non-Ironmen have dropped."

/**
 * Downgrades a Hardcore Ironman to a standard Ironman after an unsafe death. Any pending downgrade
 * is cancelled, as the wiki notes for deaths during the waiting period.
 */
public fun Player.revokeHardcoreStatus() {
    ironmanMode = IronmanMode.Standard
    hardcoreDead = 1
    downgradeType = 0
    downgradeDate = 0
}

/** Message for an Ultimate Ironman trying to open their bank. */
public const val ULTIMATE_IRONMAN_BANK_MESSAGE: String =
    "As an Ultimate Ironman, you cannot use the bank."
