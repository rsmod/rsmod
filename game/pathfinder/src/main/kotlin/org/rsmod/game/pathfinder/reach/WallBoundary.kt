@file:Suppress("DuplicatedCode")

package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag

internal fun reachWall(
    flags: CollisionFlagMap,
    x: Int,
    y: Int,
    level: Int,
    destX: Int,
    destY: Int,
    srcSize: Int,
    shape: Int,
    rot: Int
): Boolean = when {
    srcSize == 1 && x == destX && y == destY -> true
    srcSize != 1 && destX >= x && srcSize + x - 1 >= destX &&
        destY >= y && srcSize + y - 1 >= destY -> true
    srcSize == 1 -> reachWall1(
        flags,
        x,
        y,
        level,
        destX,
        destY,
        shape,
        rot
    )
    else -> reachWallN(
        flags,
        x,
        y,
        level,
        destX,
        destY,
        srcSize,
        shape,
        rot
    )
}

private fun reachWall1(
    flags: CollisionFlagMap,
    x: Int,
    y: Int,
    level: Int,
    destX: Int,
    destY: Int,
    shape: Int,
    rot: Int
): Boolean {
    when (shape) {
        0 -> {
            when (rot) {
                0 -> {
                    if (x == destX - 1 && y == destY) {
                        return true
                    } else if (
                        x == destX && y == destY + 1 && (flags[x, y, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        x == destX && y == destY - 1 && (flags[x, y, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (x == destX && y == destY + 1) {
                        return true
                    } else if (
                        x == destX - 1 && y == destY &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        x == destX + 1 && y == destY &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (x == destX + 1 && y == destY) {
                        return true
                    } else if (
                        x == destX && y == destY + 1 &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        x == destX && y == destY - 1 &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                3 -> {
                    if (x == destX && y == destY - 1) {
                        return true
                    } else if (
                        x == destX - 1 && y == destY &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        x == destX + 1 && y == destY &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
            }
        }
        2 -> {
            when (rot) {
                0 -> {
                    if (x == destX - 1 && y == destY) {
                        return true
                    } else if (x == destX && y == destY + 1) {
                        return true
                    } else if (
                        x == destX + 1 && y == destY &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (
                        x == destX && y == destY - 1 &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (
                        x == destX - 1 && y == destY &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (x == destX && y == destY + 1) {
                        return true
                    } else if (x == destX + 1 && y == destY) {
                        return true
                    } else if (
                        x == destX && y == destY - 1 &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (
                        x == destX - 1 && y == destY &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        x == destX && y == destY + 1 &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (x == destX + 1 && y == destY) {
                        return true
                    } else if (x == destX && y == destY - 1) {
                        return true
                    }
                }
                3 -> {
                    if (x == destX - 1 && y == destY) {
                        return true
                    } else if (
                        x == destX && y == destY + 1 &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        x == destX + 1 && y == destY &&
                        (flags[x, y, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (x == destX && y == destY - 1) {
                        return true
                    }
                }
            }
        }
        9 -> {
            if (
                x == destX && y == destY + 1 &&
                (flags[x, y, level] and CollisionFlag.WALL_SOUTH) == 0
            ) {
                return true
            } else if (
                x == destX && y == destY - 1 &&
                (flags[x, y, level] and CollisionFlag.WALL_NORTH) == 0
            ) {
                return true
            } else if (
                x == destX - 1 && y == destY &&
                (flags[x, y, level] and CollisionFlag.WALL_EAST) == 0
            ) {
                return true
            }
            return x == destX + 1 && y == destY &&
                (flags[x, y, level] and CollisionFlag.WALL_WEST) == 0
        }
    }
    return false
}

private fun reachWallN(
    flags: CollisionFlagMap,
    x: Int,
    y: Int,
    level: Int,
    destX: Int,
    destY: Int,
    srcSize: Int,
    shape: Int,
    rot: Int
): Boolean {
    val east = x + srcSize - 1
    val north = y + srcSize - 1
    when (shape) {
        0 -> {
            when (rot) {
                0 -> {
                    if (x == destX - srcSize && y <= destY && north >= destY) {
                        return true
                    } else if (
                        destX in x..east && y == destY + 1 &&
                        (flags[destX, y, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (destX in x..east && y == destY - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (destX in x..east && y == destY + 1) {
                        return true
                    } else if (
                        x == destX - srcSize && y <= destY && north >= destY &&
                        (flags[east, destY, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (x == destX + 1 && y <= destY && north >= destY &&
                        (flags[x, destY, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (x == destX + 1 && y <= destY && north >= destY) {
                        return true
                    } else if (
                        destX in x..east && y == destY + 1 &&
                        (flags[destX, y, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        destX in x..east && y == destY - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                3 -> {
                    if (destX in x..east && y == destY - srcSize) {
                        return true
                    } else if (
                        x == destX - srcSize && y <= destY && north >= destY &&
                        (flags[east, destY, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        x == destX + 1 && y <= destY && north >= destY &&
                        (flags[x, destY, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
            }
        }
        2 -> {
            when (rot) {
                0 -> {
                    if (x == destX - srcSize && y <= destY && north >= destY) {
                        return true
                    } else if (destX in x..east && y == destY + 1) {
                        return true
                    } else if (
                        x == destX + 1 && y <= destY && north >= destY &&
                        (flags[x, destY, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (destX in x..east && y == destY - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (
                        x == destX - srcSize && y <= destY && north >= destY &&
                        (flags[east, destY, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (destX in x..east && y == destY + 1) {
                        return true
                    } else if (x == destX + 1 && y <= destY && north >= destY) {
                        return true
                    } else if (
                        destX in x..east && y == destY - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (
                        x == destX - srcSize && y <= destY && north >= destY &&
                        (flags[east, destY, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        destX in x..east && y == destY + 1 &&
                        (flags[destX, y, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (x == destX + 1 && y <= destY && north >= destY) {
                        return true
                    } else if (destX in x..east && y == destY - srcSize) {
                        return true
                    }
                }
                3 -> {
                    if (x == destX - srcSize && y <= destY && north >= destY) {
                        return true
                    } else if (
                        destX in x..east && y == destY + 1 &&
                        (flags[destX, y, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        x == destX + 1 && y <= destY && north >= destY &&
                        (flags[x, destY, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (destX in x..east && y == destY - srcSize) {
                        return true
                    }
                }
            }
        }
        9 -> {
            if (
                destX in x..east && y == destY + 1 &&
                (flags[destX, y, level] and CollisionFlag.BLOCK_NORTH) == 0
            ) {
                return true
            } else if (
                destX in x..east && y == destY - srcSize &&
                (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
            ) {
                return true
            } else if (
                x == destX - srcSize && y <= destY && north >= destY &&
                (flags[east, destY, level] and CollisionFlag.BLOCK_WEST) == 0
            ) {
                return true
            }
            return x == destX + 1 && y <= destY && north >= destY &&
                (flags[x, destY, level] and CollisionFlag.BLOCK_EAST) == 0
        }
    }
    return false
}
