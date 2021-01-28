package org.rsmod.plugins.api.collision

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.domain.translate
import org.rsmod.game.model.map.Coordinates
import org.rsmod.pathfinder.collision.CollisionStrategies
import org.rsmod.pathfinder.collision.CollisionStrategy
import org.rsmod.pathfinder.flag.CollisionFlag

fun CollisionMap.canTraverse(
    src: Coordinates,
    dir: Direction,
    strategy: CollisionStrategy = CollisionStrategies.Normal
): Boolean {
    // TODO: support for size2/n
    val dest = src.translate(dir)
    val flag = this[dest] ?: 0
    return when (dir) {
        Direction.West -> strategy.canMove(flag, CollisionFlag.BLOCK_WEST)
        Direction.East -> strategy.canMove(flag, CollisionFlag.BLOCK_EAST)
        Direction.North -> strategy.canMove(flag, CollisionFlag.BLOCK_NORTH)
        Direction.South -> strategy.canMove(flag, CollisionFlag.BLOCK_SOUTH)
        Direction.SouthWest -> strategy.canMove(flag, CollisionFlag.BLOCK_SOUTH_WEST)
                && strategy.canMove(flag, CollisionFlag.BLOCK_WEST)
                && strategy.canMove(flag, CollisionFlag.BLOCK_SOUTH)
        Direction.SouthEast -> strategy.canMove(flag, CollisionFlag.BLOCK_SOUTH_EAST)
                && strategy.canMove(flag, CollisionFlag.BLOCK_EAST)
                && strategy.canMove(flag, CollisionFlag.BLOCK_SOUTH)
        Direction.NorthWest -> strategy.canMove(flag, CollisionFlag.BLOCK_NORTH_WEST)
                && strategy.canMove(flag, CollisionFlag.BLOCK_WEST)
                && strategy.canMove(flag, CollisionFlag.BLOCK_NORTH)
        Direction.NorthEast -> strategy.canMove(flag, CollisionFlag.BLOCK_NORTH_EAST)
                && strategy.canMove(flag, CollisionFlag.BLOCK_EAST)
                && strategy.canMove(flag, CollisionFlag.BLOCK_NORTH)
    }
}
