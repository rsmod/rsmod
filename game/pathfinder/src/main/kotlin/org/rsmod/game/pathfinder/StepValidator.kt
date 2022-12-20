package org.rsmod.game.pathfinder

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.collision.CollisionStrategies
import org.rsmod.game.pathfinder.collision.CollisionStrategy
import org.rsmod.game.pathfinder.flag.CollisionFlag
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NORTH_AND_SOUTH_EAST
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NORTH_AND_SOUTH_WEST
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_NORTH_EAST_AND_WEST
import org.rsmod.game.pathfinder.flag.CollisionFlag.BLOCK_SOUTH_EAST_AND_WEST

/**
 * @author Kris | 16/03/2022
 */
public class StepValidator(private val flags: CollisionFlagMap) {

    public fun canTravel(
        level: Int,
        x: Int,
        y: Int,
        offsetX: Int,
        offsetY: Int,
        size: Int = 1,
        collision: CollisionStrategy = CollisionStrategies.Normal,
    ): Boolean {
        assert(offsetX in -1..1) { "Offset x must be in bounds of -1..1" }
        assert(offsetY in -1..1) { "Offset y must be in bounds of -1..1" }
        assert(offsetX != 0 || offsetY != 0) { "Offset x and y cannot both be 0." }
        val blocked = when (getDirection(offsetX, offsetY)) {
            Direction.South -> isBlockedSouth(level, x, y, size, collision)
            Direction.North -> isBlockedNorth(level, x, y, size, collision)
            Direction.West -> isBlockedWest(level, x, y, size, collision)
            Direction.East -> isBlockedEast(level, x, y, size, collision)
            Direction.SouthWest -> isBlockedSouthWest(level, x, y, size, collision)
            Direction.NorthWest -> isBlockedNorthWest(level, x, y, size, collision)
            Direction.SouthEast -> isBlockedSouthEast(level, x, y, size, collision)
            Direction.NorthEast -> isBlockedNorthEast(level, x, y, size, collision)
        }
        return !blocked
    }

    private fun isBlockedSouth(level: Int, x: Int, y: Int, size: Int, collision: CollisionStrategy): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x, y - 1, level], CollisionFlag.BLOCK_SOUTH)
            2 -> !collision.canMove(flags[x, y - 1, level], CollisionFlag.BLOCK_SOUTH_WEST) ||
                !collision.canMove(flags[x + 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_EAST)
            else -> {
                if (
                    !collision.canMove(flags[x, y - 1, level], CollisionFlag.BLOCK_SOUTH_WEST) ||
                    !collision.canMove(flags[x + size - 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_EAST)
                ) {
                    return true
                }
                for (midX in x + 1 until x + size - 1) {
                    if (!collision.canMove(flags[midX, y - 1, level], BLOCK_NORTH_EAST_AND_WEST)) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedNorth(level: Int, x: Int, y: Int, size: Int, collision: CollisionStrategy): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x, y + 1, level], CollisionFlag.BLOCK_NORTH)
            2 -> !collision.canMove(flags[x, y + 2, level], CollisionFlag.BLOCK_NORTH_WEST) ||
                !collision.canMove(flags[x + 1, y + 2, level], CollisionFlag.BLOCK_NORTH_EAST)
            else -> {
                if (
                    !collision.canMove(flags[x, y + size, level], CollisionFlag.BLOCK_NORTH_WEST) ||
                    !collision.canMove(flags[x + size - 1, y + size, level], CollisionFlag.BLOCK_NORTH_EAST)
                ) {
                    return true
                }
                for (midX in x + 1 until x + size - 1) {
                    if (!collision.canMove(flags[midX, y + size, level], BLOCK_SOUTH_EAST_AND_WEST)) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedWest(level: Int, x: Int, y: Int, size: Int, collision: CollisionStrategy): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, y, level], CollisionFlag.BLOCK_WEST)
            2 -> !collision.canMove(flags[x - 1, y, level], CollisionFlag.BLOCK_SOUTH_WEST) ||
                !collision.canMove(flags[x - 1, y + 1, level], CollisionFlag.BLOCK_NORTH_WEST)
            else -> {
                if (
                    !collision.canMove(flags[x - 1, y, level], CollisionFlag.BLOCK_SOUTH_WEST) ||
                    !collision.canMove(flags[x - 1, y + size - 1, level], CollisionFlag.BLOCK_NORTH_WEST)
                ) {
                    return true
                }
                for (midY in y + 1 until y + size - 1) {
                    if (!collision.canMove(flags[x - 1, midY, level], BLOCK_NORTH_AND_SOUTH_EAST)) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedEast(level: Int, x: Int, y: Int, size: Int, collision: CollisionStrategy): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, y, level], CollisionFlag.BLOCK_EAST)
            2 -> !collision.canMove(flags[x + 2, y, level], CollisionFlag.BLOCK_SOUTH_EAST) ||
                !collision.canMove(flags[x + 2, y + 1, level], CollisionFlag.BLOCK_NORTH_EAST)
            else -> {
                if (
                    !collision.canMove(flags[x + size, y, level], CollisionFlag.BLOCK_SOUTH_EAST) ||
                    !collision.canMove(flags[x + size, y + size - 1, level], CollisionFlag.BLOCK_NORTH_EAST)
                ) {
                    return true
                }
                for (midY in y + 1 until y + size - 1) {
                    if (!collision.canMove(flags[x + size, midY, level], BLOCK_NORTH_AND_SOUTH_WEST)) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedSouthWest(level: Int, x: Int, y: Int, size: Int, collision: CollisionStrategy): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_WEST) ||
                !collision.canMove(flags[x - 1, y, level], CollisionFlag.BLOCK_WEST) ||
                !collision.canMove(flags[x, y - 1, level], CollisionFlag.BLOCK_SOUTH)
            2 -> !collision.canMove(flags[x - 1, y, level], BLOCK_NORTH_AND_SOUTH_EAST) ||
                !collision.canMove(flags[x - 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_WEST) ||
                !collision.canMove(flags[x, y - 1, level], BLOCK_NORTH_EAST_AND_WEST)
            else -> {
                if (!collision.canMove(flags[x - 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_WEST)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(flags[x - 1, y + mid - 1, level], BLOCK_NORTH_AND_SOUTH_EAST) ||
                        !collision.canMove(flags[x + mid - 1, y - 1, level], BLOCK_NORTH_EAST_AND_WEST)
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedNorthWest(level: Int, x: Int, y: Int, size: Int, collision: CollisionStrategy): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, y + 1, level], CollisionFlag.BLOCK_NORTH_WEST) ||
                !collision.canMove(flags[x - 1, y, level], CollisionFlag.BLOCK_WEST) ||
                !collision.canMove(flags[x, y + 1, level], CollisionFlag.BLOCK_NORTH)
            2 -> !collision.canMove(flags[x - 1, y + 1, level], BLOCK_NORTH_AND_SOUTH_EAST) ||
                !collision.canMove(flags[x - 1, y + 2, level], CollisionFlag.BLOCK_NORTH_WEST) ||
                !collision.canMove(flags[x, y + 2, level], BLOCK_SOUTH_EAST_AND_WEST)
            else -> {
                if (!collision.canMove(flags[x - 1, y + size, level], CollisionFlag.BLOCK_NORTH_WEST)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(flags[x - 1, y + mid, level], BLOCK_NORTH_AND_SOUTH_EAST) ||
                        !collision.canMove(flags[x + mid - 1, y + size, level], BLOCK_SOUTH_EAST_AND_WEST)
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedSouthEast(level: Int, x: Int, y: Int, size: Int, collision: CollisionStrategy): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_EAST) ||
                !collision.canMove(flags[x + 1, y, level], CollisionFlag.BLOCK_EAST) ||
                !collision.canMove(flags[x, y - 1, level], CollisionFlag.BLOCK_SOUTH)
            2 -> !collision.canMove(flags[x + 1, y - 1, level], BLOCK_NORTH_EAST_AND_WEST) ||
                !collision.canMove(flags[x + 2, y - 1, level], CollisionFlag.BLOCK_SOUTH_EAST) ||
                !collision.canMove(flags[x + 2, y, level], BLOCK_NORTH_AND_SOUTH_WEST)
            else -> {
                if (!collision.canMove(
                        flags[x + size, y - 1, level],
                        CollisionFlag.BLOCK_SOUTH_EAST
                    )
                ) return true
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(flags[x + size, y + mid - 1, level], BLOCK_NORTH_AND_SOUTH_WEST) ||
                        !collision.canMove(flags[x + mid, y - 1, level], BLOCK_NORTH_EAST_AND_WEST)
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedNorthEast(level: Int, x: Int, y: Int, size: Int, collision: CollisionStrategy): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, y + 1, level], CollisionFlag.BLOCK_NORTH_EAST) ||
                !collision.canMove(flags[x + 1, y, level], CollisionFlag.BLOCK_EAST) ||
                !collision.canMove(flags[x, y + 1, level], CollisionFlag.BLOCK_NORTH)
            2 -> !collision.canMove(flags[x + 1, y + 2, level], BLOCK_SOUTH_EAST_AND_WEST) ||
                !collision.canMove(flags[x + 2, y + 2, level], CollisionFlag.BLOCK_NORTH_EAST) ||
                !collision.canMove(flags[x + 2, y + 1, level], BLOCK_NORTH_AND_SOUTH_WEST)
            else -> {
                if (!collision.canMove(flags[x + size, y + size, level], CollisionFlag.BLOCK_NORTH_EAST)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(flags[x + mid, y + size, level], BLOCK_SOUTH_EAST_AND_WEST) ||
                        !collision.canMove(flags[x + size, y + mid, level], BLOCK_NORTH_AND_SOUTH_WEST)
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private companion object {

        private val mappedDirections = List(0xF) { key ->
            Direction.values.firstOrNull { bitpackDirection(it.offX, it.offY) == key }
        }

        private fun getDirection(xOff: Int, yOff: Int): Direction {
            assert(xOff in -1..1) { "X offset must be in bounds of -1..1" }
            assert(yOff in -1..1) { "Y offset must be in bounds of -1..1" }
            return mappedDirections[bitpackDirection(xOff, yOff)]
                ?: throw IllegalArgumentException("Offsets [$xOff, $yOff] do not produce a valid movement direction.")
        }

        private fun bitpackDirection(xOff: Int, yOff: Int): Int = xOff.inc().shl(2) or yOff.inc()
    }
}
