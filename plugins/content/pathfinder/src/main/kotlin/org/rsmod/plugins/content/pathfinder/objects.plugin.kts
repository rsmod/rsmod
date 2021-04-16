package org.rsmod.plugins.content.pathfinder

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.collision.buildFlags
import org.rsmod.game.coroutine.delay
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.obj.type.ObjectType
import org.rsmod.pathfinder.SmartPathFinder
import org.rsmod.plugins.api.model.mob.player.GameMessage
import org.rsmod.plugins.api.model.mob.player.clearMinimapFlag
import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.model.mob.player.sendMinimapFlag
import org.rsmod.plugins.api.protocol.packet.MoveType
import org.rsmod.plugins.api.protocol.packet.ObjectAction
import org.rsmod.plugins.api.protocol.packet.ObjectClick

val collision: CollisionMap by inject()

onAction<ObjectClick> {
    val speed = when (moveType) {
        MoveType.Displace -> {
            player.stopMovement()
            player.displace(coords)
            return@onAction
        }
        MoveType.ForceWalk -> MovementSpeed.Walk
        MoveType.ForceRun -> MovementSpeed.Run
        else -> null
    }
    if (approach) {
        player.publishAction(action, type)
        return@onAction
    }
    val pf = SmartPathFinder()
    val route = pf.findPath(
        flags = collision.buildFlags(player.coords, pf.searchMapSize),
        srcX = player.coords.x,
        srcY = player.coords.y,
        destX = coords.x,
        destY = coords.y,
        destWidth = if (rot == 0 || rot == 2) type.width else type.height,
        destHeight = if (rot == 0 || rot == 2) type.height else type.width,
        objRot = rot,
        objShape = shape,
        accessBitMask = accessBitMask(rot, type.clipMask)
    )
    val coordsList = route.map { Coordinates(it.x, it.y, player.coords.level) }
    player.clearQueues()
    player.movement.clear()
    player.movement.speed = speed
    player.movement.addAll(coordsList)
    if (coordsList.isEmpty()) {
        player.clearMinimapFlag()
        if (route.failed) {
            player.sendMessage(GameMessage.CANNOT_REACH_THAT)
        } else if (!route.alternative) {
            player.publishAction(action, type)
        }
        return@onAction
    }
    val destCoords = coordsList.last()
    player.sendMinimapFlag(destCoords.x, destCoords.y)
    player.normalQueue {
        delay()
        var reached = false
        while (!reached) {
            if (player.coords == destCoords) {
                reached = true
                break
            }
            delay()
        }
        if (!reached || route.alternative) {
            player.sendMessage(GameMessage.CANNOT_REACH_THAT)
            return@normalQueue
        }
        player.publishAction(action, type)
    }
}

fun Player.publishAction(action: ObjectAction, type: ObjectType) {
    val published = actionBus.publish(action, type.id)
    if (!published) {
        warn { "Unhandled object action: $action" }
    }
}

fun accessBitMask(rot: Int, mask: Int): Int = if (rot == 0) {
    mask
} else {
    ((mask shl rot) and 0xF) + (mask shr (4 - rot))
}
