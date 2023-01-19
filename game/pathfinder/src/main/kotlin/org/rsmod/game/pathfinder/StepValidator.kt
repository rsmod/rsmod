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
        extraFlag: Int = 0,
        collision: CollisionStrategy = CollisionStrategies.Normal,
    ): Boolean {
        assert(offsetX in -1..1) { "Offset x must be in bounds of -1..1" }
        assert(offsetY in -1..1) { "Offset y must be in bounds of -1..1" }
        assert(offsetX != 0 || offsetY != 0) { "Offset x and y cannot both be 0." }
        val blocked = when (getDirection(offsetX, offsetY)) {
            Direction.South -> isBlockedSouth(level, x, y, size, extraFlag, collision)
            Direction.North -> isBlockedNorth(level, x, y, size, extraFlag, collision)
            Direction.West -> isBlockedWest(level, x, y, size, extraFlag, collision)
            Direction.East -> isBlockedEast(level, x, y, size, extraFlag, collision)
            Direction.SouthWest -> isBlockedSouthWest(level, x, y, size, extraFlag, collision)
            Direction.NorthWest -> isBlockedNorthWest(level, x, y, size, extraFlag, collision)
            Direction.SouthEast -> isBlockedSouthEast(level, x, y, size, extraFlag, collision)
            Direction.NorthEast -> isBlockedNorthEast(level, x, y, size, extraFlag, collision)
        }
        return !blocked
    }

    private fun isBlockedSouth(
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x, y - 1, level], CollisionFlag.BLOCK_SOUTH or extraFlag)
            2 -> !collision.canMove(flags[x, y - 1, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x + 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x, y - 1, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                    !collision.canMove(flags[x + size - 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag)
                ) {
                    return true
                }
                for (midX in x + 1 until x + size - 1) {
                    if (!collision.canMove(flags[midX, y - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag)) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedNorth(
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x, y + 1, level], CollisionFlag.BLOCK_NORTH or extraFlag)
            2 -> !collision.canMove(flags[x, y + 2, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag) ||
                !collision.canMove(flags[x + 1, y + 2, level], CollisionFlag.BLOCK_NORTH_EAST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x, y + size, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag) ||
                    !collision.canMove(
                        flags[x + size - 1, y + size, level],
                        CollisionFlag.BLOCK_NORTH_EAST or extraFlag
                    )
                ) {
                    return true
                }
                for (midX in x + 1 until x + size - 1) {
                    if (!collision.canMove(flags[midX, y + size, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag)) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedWest(
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, y, level], CollisionFlag.BLOCK_WEST or extraFlag)
            2 -> !collision.canMove(flags[x - 1, y, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x - 1, y + 1, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x - 1, y, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                    !collision.canMove(flags[x - 1, y + size - 1, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag)
                ) {
                    return true
                }
                for (midY in y + 1 until y + size - 1) {
                    if (!collision.canMove(flags[x - 1, midY, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag)) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedEast(
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, y, level], CollisionFlag.BLOCK_EAST or extraFlag)
            2 -> !collision.canMove(flags[x + 2, y, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 2, y + 1, level], CollisionFlag.BLOCK_NORTH_EAST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x + size, y, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag) ||
                    !collision.canMove(
                        flags[x + size, y + size - 1, level],
                        CollisionFlag.BLOCK_NORTH_EAST or extraFlag
                    )
                ) {
                    return true
                }
                for (midY in y + 1 until y + size - 1) {
                    if (!collision.canMove(flags[x + size, midY, level], BLOCK_NORTH_AND_SOUTH_WEST or extraFlag)) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedSouthWest(
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x - 1, y, level], CollisionFlag.BLOCK_WEST or extraFlag) ||
                !collision.canMove(flags[x, y - 1, level], CollisionFlag.BLOCK_SOUTH or extraFlag)
            2 -> !collision.canMove(flags[x - 1, y, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x - 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x, y - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag)
            else -> {
                if (!collision.canMove(flags[x - 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(flags[x - 1, y + mid - 1, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag) ||
                        !collision.canMove(flags[x + mid - 1, y - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag)
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedNorthWest(
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, y + 1, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag) ||
                !collision.canMove(flags[x - 1, y, level], CollisionFlag.BLOCK_WEST or extraFlag) ||
                !collision.canMove(flags[x, y + 1, level], CollisionFlag.BLOCK_NORTH or extraFlag)
            2 -> !collision.canMove(flags[x - 1, y + 1, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x - 1, y + 2, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag) ||
                !collision.canMove(flags[x, y + 2, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag)
            else -> {
                if (!collision.canMove(flags[x - 1, y + size, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(flags[x - 1, y + mid, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag) ||
                        !collision.canMove(flags[x + mid - 1, y + size, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag)
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedSouthEast(
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, y - 1, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 1, y, level], CollisionFlag.BLOCK_EAST or extraFlag) ||
                !collision.canMove(flags[x, y - 1, level], CollisionFlag.BLOCK_SOUTH or extraFlag)
            2 -> !collision.canMove(flags[x + 1, y - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag) ||
                !collision.canMove(flags[x + 2, y - 1, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 2, y, level], BLOCK_NORTH_AND_SOUTH_WEST or extraFlag)
            else -> {
                if (!collision.canMove(
                        flags[x + size, y - 1, level],
                        CollisionFlag.BLOCK_SOUTH_EAST or extraFlag
                    )
                ) return true
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(
                            flags[x + size, y + mid - 1, level],
                            BLOCK_NORTH_AND_SOUTH_WEST or extraFlag
                        ) ||
                        !collision.canMove(flags[x + mid, y - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag)
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedNorthEast(
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, y + 1, level], CollisionFlag.BLOCK_NORTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 1, y, level], CollisionFlag.BLOCK_EAST or extraFlag) ||
                !collision.canMove(flags[x, y + 1, level], CollisionFlag.BLOCK_NORTH or extraFlag)
            2 -> !collision.canMove(flags[x + 1, y + 2, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag) ||
                !collision.canMove(flags[x + 2, y + 2, level], CollisionFlag.BLOCK_NORTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 2, y + 1, level], BLOCK_NORTH_AND_SOUTH_WEST or extraFlag)
            else -> {
                if (!collision.canMove(flags[x + size, y + size, level], CollisionFlag.BLOCK_NORTH_EAST or extraFlag)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(flags[x + mid, y + size, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag) ||
                        !collision.canMove(flags[x + size, y + mid, level], BLOCK_NORTH_AND_SOUTH_WEST or extraFlag)
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    public companion object {

        private val mappedDirections = List(0xF) { key ->
            Direction.values.firstOrNull { bitpackDirection(it.offX, it.offY) == key }
        }

        /**
         * Calculates coordinates for [sourceX]/[sourceY] to move to interact with [targetX]/[targetY]
         * We first determine the cardinal direction of the source relative to the target by comparing if
         * the source lies to the left or right of diagonal \ and anti-diagonal / lines.
         * \ <= North <= /
         *  +------------+  >
         *  |            |  East
         *  +------------+  <
         * / <= South <= \
         * We then further bisect the area into three section relative to the south-west tile (zero):
         * 1. Greater than zero: follow their diagonal until the target side is reached (clamped at the furthest most tile)
         * 2. Less than zero: zero minus the size of the source
         * 3. Equal to zero: move directly towards zero / the south-west coordinate
         *
         * <  \ 0 /   <   /
         *     +---------+
         *     |         |
         *     +---------+
         * This method is equivalent to returning the last coordinate in a sequence of steps towards south-west when moving
         * ordinal then cardinally until entity side comes into contact with another.
         */
        public fun naiveDestination(
            sourceX: Int,
            sourceY: Int,
            sourceWidth: Int,
            sourceHeight: Int,
            targetX: Int,
            targetY: Int,
            targetWidth: Int,
            targetHeight: Int
        ): RouteCoordinates {
            val diagonal = (sourceX - targetX) + (sourceY - targetY)
            val anti = (sourceX - targetX) - (sourceY - targetY)
            val southWestClockwise = anti < 0
            val northWestClockwise = diagonal >= (targetHeight - 1) - (sourceWidth - 1)
            val northEastClockwise = anti > sourceWidth - sourceHeight
            val southEastClockwise = diagonal <= (targetWidth - 1) - (sourceHeight - 1)

            val target = RouteCoordinates(targetX, targetY)
            if (southWestClockwise && !northWestClockwise) {
                val offY = when { // West
                    diagonal >= -sourceWidth -> (diagonal + sourceWidth).coerceAtMost(targetHeight - 1)
                    anti > -sourceWidth -> -(sourceWidth + anti)
                    else -> 0
                }
                return target.translate(-sourceWidth, offY)
            } else if (northWestClockwise && !northEastClockwise) {
                val offX = when { // North
                    anti >= -targetHeight -> (anti + targetHeight).coerceAtMost(targetWidth - 1)
                    diagonal < targetHeight -> (diagonal - targetHeight).coerceAtLeast(-(sourceWidth - 1))
                    else -> 0
                }
                return target.translate(offX, targetHeight)
            } else if (northEastClockwise && !southEastClockwise) {
                val offY = when { // East
                    anti <= targetWidth -> targetHeight - anti
                    diagonal < targetWidth -> (diagonal - targetWidth).coerceAtLeast(-(sourceHeight - 1))
                    else -> 0
                }
                return target.translate(targetWidth, offY)
            } else {
                check(southEastClockwise && !southWestClockwise)
                val offX = when { // South
                    diagonal > -sourceHeight -> (diagonal + sourceHeight).coerceAtMost(targetWidth - 1)
                    anti < sourceHeight -> (anti - sourceHeight).coerceAtLeast(-(sourceHeight - 1))
                    else -> 0
                }
                return target.translate(offX, -sourceHeight)
            }
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
