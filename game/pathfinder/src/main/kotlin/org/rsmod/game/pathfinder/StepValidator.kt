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
        z: Int,
        offsetX: Int,
        offsetZ: Int,
        size: Int = 1,
        extraFlag: Int = 0,
        collision: CollisionStrategy = CollisionStrategies.Normal
    ): Boolean {
        assert(offsetX in -1..1) { "Offset x must be in bounds of -1..1" }
        assert(offsetZ in -1..1) { "Offset z must be in bounds of -1..1" }
        assert(offsetX != 0 || offsetZ != 0) { "Offset x and z cannot both be 0." }
        val blocked = when (getDirection(offsetX, offsetZ)) {
            Direction.South -> isBlockedSouth(level, x, z, size, extraFlag, collision)
            Direction.North -> isBlockedNorth(level, x, z, size, extraFlag, collision)
            Direction.West -> isBlockedWest(level, x, z, size, extraFlag, collision)
            Direction.East -> isBlockedEast(level, x, z, size, extraFlag, collision)
            Direction.SouthWest -> isBlockedSouthWest(level, x, z, size, extraFlag, collision)
            Direction.NorthWest -> isBlockedNorthWest(level, x, z, size, extraFlag, collision)
            Direction.SouthEast -> isBlockedSouthEast(level, x, z, size, extraFlag, collision)
            Direction.NorthEast -> isBlockedNorthEast(level, x, z, size, extraFlag, collision)
        }
        return !blocked
    }

    private fun isBlockedSouth(
        level: Int,
        x: Int,
        z: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x, z - 1, level], CollisionFlag.BLOCK_SOUTH or extraFlag)
            2 -> !collision.canMove(flags[x, z - 1, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x + 1, z - 1, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x, z - 1, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                    !collision.canMove(flags[x + size - 1, z - 1, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag)
                ) {
                    return true
                }
                for (midX in x + 1 until x + size - 1) {
                    if (!collision.canMove(flags[midX, z - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag)) {
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
        z: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x, z + 1, level], CollisionFlag.BLOCK_NORTH or extraFlag)
            2 -> !collision.canMove(flags[x, z + 2, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag) ||
                !collision.canMove(flags[x + 1, z + 2, level], CollisionFlag.BLOCK_NORTH_EAST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x, z + size, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag) ||
                    !collision.canMove(
                        flags[x + size - 1, z + size, level],
                        CollisionFlag.BLOCK_NORTH_EAST or extraFlag
                    )
                ) {
                    return true
                }
                for (midX in x + 1 until x + size - 1) {
                    if (!collision.canMove(flags[midX, z + size, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag)) {
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
        z: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, z, level], CollisionFlag.BLOCK_WEST or extraFlag)
            2 -> !collision.canMove(flags[x - 1, z, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x - 1, z + 1, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x - 1, z, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                    !collision.canMove(flags[x - 1, z + size - 1, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag)
                ) {
                    return true
                }
                for (midZ in z + 1 until z + size - 1) {
                    if (!collision.canMove(flags[x - 1, midZ, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag)) {
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
        z: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, z, level], CollisionFlag.BLOCK_EAST or extraFlag)
            2 -> !collision.canMove(flags[x + 2, z, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 2, z + 1, level], CollisionFlag.BLOCK_NORTH_EAST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x + size, z, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag) ||
                    !collision.canMove(
                        flags[x + size, z + size - 1, level],
                        CollisionFlag.BLOCK_NORTH_EAST or extraFlag
                    )
                ) {
                    return true
                }
                for (midZ in z + 1 until z + size - 1) {
                    if (!collision.canMove(flags[x + size, midZ, level], BLOCK_NORTH_AND_SOUTH_WEST or extraFlag)) {
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
        z: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, z - 1, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x - 1, z, level], CollisionFlag.BLOCK_WEST or extraFlag) ||
                !collision.canMove(flags[x, z - 1, level], CollisionFlag.BLOCK_SOUTH or extraFlag)
            2 -> !collision.canMove(flags[x - 1, z, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x - 1, z - 1, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x, z - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag)
            else -> {
                if (!collision.canMove(flags[x - 1, z - 1, level], CollisionFlag.BLOCK_SOUTH_WEST or extraFlag)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(flags[x - 1, z + mid - 1, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag) ||
                        !collision.canMove(flags[x + mid - 1, z - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag)
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
        z: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, z + 1, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag) ||
                !collision.canMove(flags[x - 1, z, level], CollisionFlag.BLOCK_WEST or extraFlag) ||
                !collision.canMove(flags[x, z + 1, level], CollisionFlag.BLOCK_NORTH or extraFlag)
            2 -> !collision.canMove(flags[x - 1, z + 1, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x - 1, z + 2, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag) ||
                !collision.canMove(flags[x, z + 2, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag)
            else -> {
                if (!collision.canMove(flags[x - 1, z + size, level], CollisionFlag.BLOCK_NORTH_WEST or extraFlag)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(flags[x - 1, z + mid, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag) ||
                        !collision.canMove(flags[x + mid - 1, z + size, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag)
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
        z: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, z - 1, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 1, z, level], CollisionFlag.BLOCK_EAST or extraFlag) ||
                !collision.canMove(flags[x, z - 1, level], CollisionFlag.BLOCK_SOUTH or extraFlag)
            2 -> !collision.canMove(flags[x + 1, z - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag) ||
                !collision.canMove(flags[x + 2, z - 1, level], CollisionFlag.BLOCK_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 2, z, level], BLOCK_NORTH_AND_SOUTH_WEST or extraFlag)
            else -> {
                if (!collision.canMove(
                        flags[x + size, z - 1, level],
                        CollisionFlag.BLOCK_SOUTH_EAST or extraFlag
                    )
                ) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(
                            flags[x + size, z + mid - 1, level],
                            BLOCK_NORTH_AND_SOUTH_WEST or extraFlag
                        ) ||
                        !collision.canMove(flags[x + mid, z - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag)
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
        z: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, z + 1, level], CollisionFlag.BLOCK_NORTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 1, z, level], CollisionFlag.BLOCK_EAST or extraFlag) ||
                !collision.canMove(flags[x, z + 1, level], CollisionFlag.BLOCK_NORTH or extraFlag)
            2 -> !collision.canMove(flags[x + 1, z + 2, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag) ||
                !collision.canMove(flags[x + 2, z + 2, level], CollisionFlag.BLOCK_NORTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 2, z + 1, level], BLOCK_NORTH_AND_SOUTH_WEST or extraFlag)
            else -> {
                if (!collision.canMove(flags[x + size, z + size, level], CollisionFlag.BLOCK_NORTH_EAST or extraFlag)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(flags[x + mid, z + size, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag) ||
                        !collision.canMove(flags[x + size, z + mid, level], BLOCK_NORTH_AND_SOUTH_WEST or extraFlag)
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
            Direction.values.firstOrNull { bitpackDirection(it.offX, it.offZ) == key }
        }

        /**
         * Calculates coordinates for [sourceX]/[sourceZ] to move to interact with [targetX]/[targetZ]
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
        @Deprecated(
            message = "Use PathFinder.naiveDestination instead.",
            replaceWith = ReplaceWith(
                "PathFinder.naiveDestination(" +
                    "sourceX, sourceZ, sourceWidth, sourceHeight, " +
                    "targetX, targetZ, targetWidth, targetHeight)"
            )
        )
        public fun naiveDestination(
            sourceX: Int,
            sourceZ: Int,
            sourceWidth: Int,
            sourceHeight: Int,
            targetX: Int,
            targetZ: Int,
            targetWidth: Int,
            targetHeight: Int
        ): RouteCoordinates {
            return PathFinder.naiveDestination(
                sourceX,
                sourceZ,
                sourceWidth,
                sourceHeight,
                targetX,
                targetZ,
                targetWidth,
                targetHeight
            )
        }

        private fun getDirection(xOff: Int, zOff: Int): Direction {
            assert(xOff in -1..1) { "`xOff` must be in bounds of -1..1" }
            assert(zOff in -1..1) { "`zOff` must be in bounds of -1..1" }
            return mappedDirections[bitpackDirection(xOff, zOff)]
                ?: throw IllegalArgumentException("Offsets [$xOff, $zOff] do not produce a valid movement direction.")
        }

        private fun bitpackDirection(xOff: Int, zOff: Int): Int = xOff.inc().shl(2) or zOff.inc()
    }
}
