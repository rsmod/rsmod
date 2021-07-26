package org.rsmod.plugins.content.pathfinder

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.npc.type.NpcType
import org.rsmod.plugins.api.protocol.packet.MoveType
import org.rsmod.plugins.api.protocol.packet.NpcAction
import org.rsmod.plugins.api.protocol.packet.NpcClick

val collision: CollisionMap by inject()

onAction<NpcClick> {
    val speed = when (moveType) {
        MoveType.Displace -> {
            player.stopMovement()
            player.displace(npc.coords)
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
    player.moveTo(npc, collision, tempSpeed = speed) {
        player.publishAction(action, type)
    }
}

fun Player.publishAction(action: NpcAction, type: NpcType) {
    val published = actionBus.publish(action, type.id)
    if (!published) {
        warn { "Unhandled npc action: $action" }
    }
}
