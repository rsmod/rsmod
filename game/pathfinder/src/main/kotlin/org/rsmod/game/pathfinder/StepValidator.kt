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
        val blocked = when {
            offsetX == 0 && offsetZ == -1 -> isBlockedSouth(level, x, z, size, extraFlag, collision)
            offsetX == 0 && offsetZ == 1 -> isBlockedNorth(level, x, z, size, extraFlag, collision)
            offsetX == -1 && offsetZ == 0 -> isBlockedWest(level, x, z, size, extraFlag, collision)
            offsetX == 1 && offsetZ == 0 -> isBlockedEast(level, x, z, size, extraFlag, collision)
            offsetX == -1 && offsetZ == -1 -> isBlockedSouthWest(level, x, z, size, extraFlag, collision)
            offsetX == -1 && offsetZ == 1 -> isBlockedNorthWest(level, x, z, size, extraFlag, collision)
            offsetX == 1 && offsetZ == -1 -> isBlockedSouthEast(level, x, z, size, extraFlag, collision)
            offsetX == 1 && offsetZ == 1 -> isBlockedNorthEast(level, x, z, size, extraFlag, collision)
            else -> error("Invalid step tile offset: $offsetX, $offsetZ")
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
}
