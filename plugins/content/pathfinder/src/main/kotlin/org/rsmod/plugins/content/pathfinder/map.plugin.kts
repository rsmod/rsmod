package org.rsmod.plugins.content.pathfinder

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.plugins.api.protocol.packet.MapMove
import org.rsmod.plugins.api.protocol.packet.MoveType

val collision: CollisionMap by inject()

onAction<MapMove> {
    val speed = when (type) {
        MoveType.Displace -> {
            player.stopMovement()
            player.displace(destination)
            return@onAction
        }
        MoveType.ForceWalk -> MovementSpeed.Walk
        MoveType.ForceRun -> MovementSpeed.Run
        else -> null
    }
    player.moveTo(destination, collision, noclip = noclip, tempSpeed = speed)
}
