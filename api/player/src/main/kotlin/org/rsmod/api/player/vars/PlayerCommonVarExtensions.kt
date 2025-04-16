package org.rsmod.api.player.vars

import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.output.VarpSync
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType

public var Player.chatboxUnlocked: Boolean by boolVarBit(varbits.chatbox_unlocked)

internal var Player.enabledPrayers by intVarBit(varbits.enabled_prayers)
internal var Player.usingQuickPrayers by boolVarBit(varbits.using_quick_prayers)

private var Player.varSpeed: MoveSpeed by typeIntVarp(varps.option_run, ::getSpeed, ::getSpeedId)

public var Player.varMoveSpeed: MoveSpeed
    get() = varSpeed
    set(value) {
        varSpeed = value
        // Assign as `varSpeed` as it may not have
        // changed due to protected access.
        cachedMoveSpeed = varSpeed
    }

public fun Player.resyncVar(varp: VarpType) {
    if (varp.transmit.never) {
        return
    }
    VarpSync.writeVarp(client, varp, vars[varp])
}

public fun Player.resyncVar(varBit: VarBitType): Unit = resyncVar(varBit.baseVar)

private fun getSpeed(id: Int): MoveSpeed =
    when (id) {
        2 -> MoveSpeed.Crawl
        1 -> MoveSpeed.Run
        else -> MoveSpeed.Walk
    }

private fun getSpeedId(speed: MoveSpeed): Int =
    when (speed) {
        MoveSpeed.Crawl -> 2
        MoveSpeed.Run -> 1
        else -> 0
    }

// TODO: invert run mode setting to disable this
public fun Player.ctrlMoveSpeed(): MoveSpeed =
    if (varMoveSpeed == MoveSpeed.Run) {
        MoveSpeed.Walk
    } else {
        MoveSpeed.Run
    }
