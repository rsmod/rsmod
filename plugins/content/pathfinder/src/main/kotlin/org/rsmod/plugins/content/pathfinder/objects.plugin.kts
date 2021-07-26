package org.rsmod.plugins.content.pathfinder

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.obj.type.ObjectType
import org.rsmod.plugins.api.protocol.packet.MoveType
import org.rsmod.plugins.api.protocol.packet.ObjectAction
import org.rsmod.plugins.api.protocol.packet.ObjectClick

val collision: CollisionMap by inject()

onAction<ObjectClick> {
    val speed = when (moveType) {
        MoveType.Displace -> {
            player.stopMovement()
            player.displace(obj.coords)
            return@onAction
        }
        MoveType.ForceWalk -> MovementSpeed.Walk
        MoveType.ForceRun -> MovementSpeed.Run
        else -> null
    }
    if (approach) {
        player.publishAction(action, obj.type)
        return@onAction
    }
    player.moveTo(obj, collision, tempSpeed = speed) {
        player.publishAction(action, obj.type)
    }
}

fun Player.publishAction(action: ObjectAction, type: ObjectType) {
    val published = actionBus.publish(action, type.id)
    if (!published) {
        warn { "Unhandled object action: $action" }
    }
}
