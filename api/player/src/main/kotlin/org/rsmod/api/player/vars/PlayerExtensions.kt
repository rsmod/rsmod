package org.rsmod.api.player.vars

import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.output.VarpSync.writeVarp
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.type.varp.VarpType

public var Player.chatboxUnlocked: Boolean by boolVarp(varbits.chatbox_unlocked)

private var Player.varSpeed: MoveSpeed by typeIntVarp(varps.player_run, ::getSpeed, ::getSpeedId)

public var Player.varMoveSpeed: MoveSpeed
    get() = varSpeed
    set(value) {
        varSpeed = value
        // Assign as `varSpeed` as it may not have
        // changed due to protected access.
        cachedMoveSpeed = varSpeed
    }

public fun Player.syncVarp(varp: VarpType, value: Int) {
    vars[varp] = if (value == 0) null else value
    if (varp.canTransmit) {
        writeVarp(this, varp, value)
    }
}

public fun Player.syncVarpStr(varp: VarpType, value: String?) {
    // NOTE: Might be worth considering blank `value`s as null here to remove from var map.
    varsString[varp] = value
}

private fun getSpeed(id: Int?): MoveSpeed =
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
