package org.rsmod.plugins.content.pathfinder

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.collision.buildFlags
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Npc
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.move.MoveRequest
import org.rsmod.game.model.move.MoveRoute
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.obj.GameObject
import org.rsmod.pathfinder.DumbPathFinder
import org.rsmod.pathfinder.PathFinder
import org.rsmod.pathfinder.ProjectileValidator
import org.rsmod.pathfinder.SmartPathFinder
import org.rsmod.plugins.api.model.mob.player.GameMessage

fun Player.moveTo(
    destination: Coordinates,
    collision: CollisionMap,
    noclip: Boolean = false,
    tempSpeed: MovementSpeed? = null,
    pathFinder: PathFinder = SmartPathFinder(resetOnSearch = false),
    reachAction: (() -> Unit)? = null
) {
    moveRequest = MoveRequest(
        tempSpeed = tempSpeed,
        stopPreviousMovement = true,
        cannotReachMessage = null,
        reachAction = reachAction ?: { /* empty */ },
        buildRoute = {
            val route = if (movement.noclip || noclip) {
                val validator = ProjectileValidator()
                validator.rayCast(
                    collision.buildFlags(coords, validator.searchMapSize),
                    coords.x,
                    coords.y,
                    destination.x,
                    destination.y
                )
            } else {
                pathFinder.findPath(
                    collision.buildFlags(coords, pathFinder.searchMapSize),
                    coords.x,
                    coords.y,
                    destination.x,
                    destination.y
                )
            }
            val coords = route.map { Coordinates(it.x, it.y, coords.level) }
            MoveRoute(coords = coords, failed = route.failed, alternative = route.alternative)
        }
    )
}

fun Player.moveTo(
    npc: Npc,
    collision: CollisionMap,
    tempSpeed: MovementSpeed? = null,
    pathFinder: PathFinder = SmartPathFinder(resetOnSearch = false),
    reachAction: (() -> Unit)? = null
) {
    moveRequest = MoveRequest(
        tempSpeed = tempSpeed,
        stopPreviousMovement = true,
        cannotReachMessage = GameMessage.CANNOT_REACH_THAT,
        reachAction = reachAction ?: { /* empty */ },
        buildRoute = {
            val route = pathFinder.findPath(
                flags = collision.buildFlags(coords, pathFinder.searchMapSize),
                srcX = coords.x,
                srcY = coords.y,
                destX = npc.coords.x,
                destY = npc.coords.y,
                destWidth = npc.type.size,
                destHeight = npc.type.size,
                objShape = 10
            )
            val coords = route.map { Coordinates(it.x, it.y, coords.level) }
            MoveRoute(coords = coords, failed = route.failed, alternative = route.alternative)
        }
    )
}

fun Player.moveTo(
    obj: GameObject,
    collision: CollisionMap,
    tempSpeed: MovementSpeed? = null,
    pathFinder: PathFinder = SmartPathFinder(resetOnSearch = false),
    reachAction: (() -> Unit)? = null
) {
    val rot = obj.rotation
    val shape = obj.shape
    val type = obj.type
    val dest = obj.coords
    moveRequest = MoveRequest(
        tempSpeed = tempSpeed,
        stopPreviousMovement = true,
        cannotReachMessage = GameMessage.CANNOT_REACH_THAT,
        reachAction = reachAction ?: { /* empty */ },
        buildRoute = {
            val route = pathFinder.findPath(
                flags = collision.buildFlags(coords, pathFinder.searchMapSize),
                srcX = coords.x,
                srcY = coords.y,
                destX = dest.x,
                destY = dest.y,
                destWidth = if (rot == 0 || rot == 2) type.width else type.height,
                destHeight = if (rot == 0 || rot == 2) type.height else type.width,
                objRot = rot,
                objShape = shape,
                accessBitMask = accessBitMask(rot, type.clipMask)
            )
            val coords = route.map { Coordinates(it.x, it.y, coords.level) }
            MoveRoute(coords = coords, failed = route.failed, alternative = route.alternative)
        }
    )
}

private fun accessBitMask(rot: Int, mask: Int): Int = if (rot == 0) {
    mask
} else {
    ((mask shl rot) and 0xF) + (mask shr (4 - rot))
}

private val PathFinder.searchMapSize: Int
    get() = when (this) {
        is SmartPathFinder -> searchMapSize
        is DumbPathFinder -> searchMapSize
        else -> 128
    }
