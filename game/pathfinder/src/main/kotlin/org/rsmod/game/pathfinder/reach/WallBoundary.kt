@file:Suppress("DuplicatedCode")

package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag

internal fun reachWall(
    flags: CollisionFlagMap,
    x: Int,
    z: Int,
    level: Int,
    destX: Int,
    destZ: Int,
    srcSize: Int,
    shape: Int,
    rot: Int
): Boolean = when {
    srcSize == 1 && x == destX && z == destZ -> true
    srcSize != 1 && destX >= x && srcSize + x - 1 >= destX &&
        destZ >= z && srcSize + z - 1 >= destZ -> true
    srcSize == 1 -> reachWall1(
        flags,
        x,
        z,
        level,
        destX,
        destZ,
        shape,
        rot
    )
    else -> reachWallN(
        flags,
        x,
        z,
        level,
        destX,
        destZ,
        srcSize,
        shape,
        rot
    )
}

private fun reachWall1(
    flags: CollisionFlagMap,
    x: Int,
    z: Int,
    level: Int,
    destX: Int,
    destZ: Int,
    shape: Int,
    rot: Int
): Boolean {
    when (shape) {
        0 -> {
            when (rot) {
                0 -> {
                    if (x == destX - 1 && z == destZ) {
                        return true
                    } else if (
                        x == destX && z == destZ + 1 && (flags[x, z, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        x == destX && z == destZ - 1 && (flags[x, z, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (x == destX && z == destZ + 1) {
                        return true
                    } else if (
                        x == destX - 1 && z == destZ &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        x == destX + 1 && z == destZ &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (x == destX + 1 && z == destZ) {
                        return true
                    } else if (
                        x == destX && z == destZ + 1 &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        x == destX && z == destZ - 1 &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                3 -> {
                    if (x == destX && z == destZ - 1) {
                        return true
                    } else if (
                        x == destX - 1 && z == destZ &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        x == destX + 1 && z == destZ &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
            }
        }
        2 -> {
            when (rot) {
                0 -> {
                    if (x == destX - 1 && z == destZ) {
                        return true
                    } else if (x == destX && z == destZ + 1) {
                        return true
                    } else if (
                        x == destX + 1 && z == destZ &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (
                        x == destX && z == destZ - 1 &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (
                        x == destX - 1 && z == destZ &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (x == destX && z == destZ + 1) {
                        return true
                    } else if (x == destX + 1 && z == destZ) {
                        return true
                    } else if (
                        x == destX && z == destZ - 1 &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (
                        x == destX - 1 && z == destZ &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        x == destX && z == destZ + 1 &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (x == destX + 1 && z == destZ) {
                        return true
                    } else if (x == destX && z == destZ - 1) {
                        return true
                    }
                }
                3 -> {
                    if (x == destX - 1 && z == destZ) {
                        return true
                    } else if (
                        x == destX && z == destZ + 1 &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        x == destX + 1 && z == destZ &&
                        (flags[x, z, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (x == destX && z == destZ - 1) {
                        return true
                    }
                }
            }
        }
        9 -> {
            if (
                x == destX && z == destZ + 1 &&
                (flags[x, z, level] and CollisionFlag.WALL_SOUTH) == 0
            ) {
                return true
            } else if (
                x == destX && z == destZ - 1 &&
                (flags[x, z, level] and CollisionFlag.WALL_NORTH) == 0
            ) {
                return true
            } else if (
                x == destX - 1 && z == destZ &&
                (flags[x, z, level] and CollisionFlag.WALL_EAST) == 0
            ) {
                return true
            }
            return x == destX + 1 && z == destZ &&
                (flags[x, z, level] and CollisionFlag.WALL_WEST) == 0
        }
    }
    return false
}

private fun reachWallN(
    flags: CollisionFlagMap,
    x: Int,
    z: Int,
    level: Int,
    destX: Int,
    destZ: Int,
    srcSize: Int,
    shape: Int,
    rot: Int
): Boolean {
    val east = x + srcSize - 1
    val north = z + srcSize - 1
    when (shape) {
        0 -> {
            when (rot) {
                0 -> {
                    if (x == destX - srcSize && z <= destZ && north >= destZ) {
                        return true
                    } else if (
                        destX in x..east && z == destZ + 1 &&
                        (flags[destX, z, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (destX in x..east && z == destZ - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (destX in x..east && z == destZ + 1) {
                        return true
                    } else if (
                        x == destX - srcSize && z <= destZ && north >= destZ &&
                        (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (x == destX + 1 && z <= destZ && north >= destZ &&
                        (flags[x, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (x == destX + 1 && z <= destZ && north >= destZ) {
                        return true
                    } else if (
                        destX in x..east && z == destZ + 1 &&
                        (flags[destX, z, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        destX in x..east && z == destZ - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                3 -> {
                    if (destX in x..east && z == destZ - srcSize) {
                        return true
                    } else if (
                        x == destX - srcSize && z <= destZ && north >= destZ &&
                        (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        x == destX + 1 && z <= destZ && north >= destZ &&
                        (flags[x, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
            }
        }
        2 -> {
            when (rot) {
                0 -> {
                    if (x == destX - srcSize && z <= destZ && north >= destZ) {
                        return true
                    } else if (destX in x..east && z == destZ + 1) {
                        return true
                    } else if (
                        x == destX + 1 && z <= destZ && north >= destZ &&
                        (flags[x, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (destX in x..east && z == destZ - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (
                        x == destX - srcSize && z <= destZ && north >= destZ &&
                        (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (destX in x..east && z == destZ + 1) {
                        return true
                    } else if (x == destX + 1 && z <= destZ && north >= destZ) {
                        return true
                    } else if (
                        destX in x..east && z == destZ - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (
                        x == destX - srcSize && z <= destZ && north >= destZ &&
                        (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        destX in x..east && z == destZ + 1 &&
                        (flags[destX, z, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (x == destX + 1 && z <= destZ && north >= destZ) {
                        return true
                    } else if (destX in x..east && z == destZ - srcSize) {
                        return true
                    }
                }
                3 -> {
                    if (x == destX - srcSize && z <= destZ && north >= destZ) {
                        return true
                    } else if (
                        destX in x..east && z == destZ + 1 &&
                        (flags[destX, z, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        x == destX + 1 && z <= destZ && north >= destZ &&
                        (flags[x, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (destX in x..east && z == destZ - srcSize) {
                        return true
                    }
                }
            }
        }
        9 -> {
            if (
                destX in x..east && z == destZ + 1 &&
                (flags[destX, z, level] and CollisionFlag.BLOCK_NORTH) == 0
            ) {
                return true
            } else if (
                destX in x..east && z == destZ - srcSize &&
                (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
            ) {
                return true
            } else if (
                x == destX - srcSize && z <= destZ && north >= destZ &&
                (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
            ) {
                return true
            }
            return x == destX + 1 && z <= destZ && north >= destZ &&
                (flags[x, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
        }
    }
    return false
}
