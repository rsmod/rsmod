package org.rsmod.api.player

import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.vars.boolVarp
import org.rsmod.api.player.vars.typeIntVarp
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed

public var Player.chatboxUnlocked: Boolean by boolVarp(varbits.chatbox_unlocked)

public var Player.varMoveSpeed: MoveSpeed
    get() = varSpeed
    set(value) {
        varSpeed = value
        // Assign as `varSpeed` as it may not have
        // changed due to protected access.
        cachedMoveSpeed = varSpeed
    }

private var Player.varSpeed: MoveSpeed by typeIntVarp(varps.player_run, ::getSpeed, ::getSpeedId)

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
