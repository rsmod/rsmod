package org.rsmod.plugins.content.pathfinder

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.collision.buildFlags
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.pathfinder.ProjectileValidator
import org.rsmod.pathfinder.SmartPathFinder
import org.rsmod.plugins.api.model.mob.player.clearMinimapFlag
import org.rsmod.plugins.api.model.mob.player.sendMinimapFlag
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
    val route = if (player.movement.noclip || noclip) {
        val pf = ProjectileValidator()
        pf.rayCast(
            collision.buildFlags(player.coords, pf.searchMapSize),
            player.coords.x,
            player.coords.y,
            destination.x,
            destination.y
        )
    } else {
        val pf = SmartPathFinder()
        pf.findPath(
            collision.buildFlags(player.coords, pf.searchMapSize),
            player.coords.x,
            player.coords.y,
            destination.x,
            destination.y
        )
    }
    val coordsList = route.map { Coordinates(it.x, it.y, player.coords.level) }
    player.clearQueues()
    player.stopMovement()
    player.movement.speed = speed
    player.movement.addAll(coordsList)
    if (route.alternative && coordsList.isNotEmpty()) {
        val dest = coordsList.last()
        player.sendMinimapFlag(dest.x, dest.y)
    } else if (route.failed) {
        player.clearMinimapFlag()
    }
}
