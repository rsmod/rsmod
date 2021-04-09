package org.rsmod.plugins.api.update

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Mob
import org.rsmod.game.model.move.MovementQueue
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.model.move.Step
import org.rsmod.plugins.api.collision.canTraverse

internal val MovementSpeed.stepCount: Int
    get() = when (this) {
        MovementSpeed.Run -> 2
        MovementSpeed.Walk -> 1
    }

internal fun MovementQueue.pollSteps(src: Coordinates, speed: MovementSpeed, collision: CollisionMap) {
    var lastCoords = src
    for (i in 0 until speed.stepCount) {
        val dest = poll() ?: break
        val dir = directionBetween(lastCoords, dest)
        if (!noclip && !collision.canTraverse(lastCoords, dir)) {
            break
        }
        val step = Step(dest, dir)
        nextSteps.add(step)
        lastCoords = dest
    }
}

internal fun Mob.speed(): MovementSpeed {
    return movement.speed ?: speed
}

private fun directionBetween(start: Coordinates, end: Coordinates): Direction {
    val diffX = end.x - start.x
    val diffY = end.y - start.y
    return when {
        diffX > 0 && diffY > 0 -> Direction.NorthEast
        diffX > 0 && diffY == 0 -> Direction.East
        diffX > 0 && diffY < 0 -> Direction.SouthEast
        diffX < 0 && diffY > 0 -> Direction.NorthWest
        diffX < 0 && diffY == 0 -> Direction.West
        diffX < 0 && diffY < 0 -> Direction.SouthWest
        diffX == 0 && diffY > 0 -> Direction.North
        else -> Direction.South
    }
}
