package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag

internal fun reachWallDeco(
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
    srcSize == 1 && x == destX && destZ == z -> true
    srcSize != 1 && destX >= x && srcSize + x - 1 >= destX &&
        destZ >= z && srcSize + z - 1 >= destZ -> true
    srcSize == 1 -> reachWallDeco1(
        flags,
        x,
        z,
        level,
        destX,
        destZ,
        shape,
        rot
    )
    else -> reachWallDecoN(
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

@Suppress("DuplicatedCode")
private fun reachWallDeco1(
    flags: CollisionFlagMap,
    x: Int,
    z: Int,
    level: Int,
    destX: Int,
    destZ: Int,
    shape: Int,
    rot: Int
): Boolean {
    if (shape in 6..7) {
        when (rot.alteredRotation(shape)) {
            0 -> {
                if (
                    x == destX + 1 && z == destZ &&
                    (flags[x, z, level] and CollisionFlag.WALL_WEST) == 0
                ) {
                    return true
                } else if (
                    x == destX && z == destZ - 1 &&
                    (flags[x, z, level] and CollisionFlag.WALL_NORTH) == 0
                ) {
                    return true
                }
            }
            1 -> {
                if (
                    x == destX - 1 && z == destZ &&
                    (flags[x, z, level] and CollisionFlag.WALL_EAST) == 0
                ) {
                    return true
                } else if (
                    x == destX && z == destZ - 1 &&
                    (flags[x, z, level] and CollisionFlag.WALL_NORTH) == 0
                ) {
                    return true
                }
            }
            2 -> {
                if (
                    x == destX - 1 && z == destZ &&
                    (flags[x, z, level] and CollisionFlag.WALL_EAST) == 0
                ) {
                    return true
                } else if (
                    x == destX && z == destZ + 1 &&
                    (flags[x, z, level] and CollisionFlag.WALL_SOUTH) == 0
                ) {
                    return true
                }
            }
            3 -> {
                if (
                    x == destX + 1 && z == destZ &&
                    (flags[x, z, level] and CollisionFlag.WALL_WEST) == 0
                ) {
                    return true
                } else if (
                    x == destX && z == destZ + 1 &&
                    (flags[x, z, level] and CollisionFlag.WALL_SOUTH) == 0
                ) {
                    return true
                }
            }
        }
    } else if (shape == 8) {
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
    return false
}

private fun reachWallDecoN(
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
    if (shape in 6..7) {
        when (rot.alteredRotation(shape)) {
            0 -> {
                if (
                    x == destX + 1 && z <= destZ && north >= destZ &&
                    (flags[x, destZ, level] and CollisionFlag.WALL_WEST) == 0
                ) {
                    return true
                } else if (x <= destX && z == destZ - srcSize && east >= destX &&
                    (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
                ) {
                    return true
                }
            }
            1 -> {
                if (
                    x == destX - srcSize && z <= destZ && north >= destZ &&
                    (flags[east, destZ, level] and CollisionFlag.WALL_EAST) == 0
                ) {
                    return true
                } else if (
                    x <= destX && z == destZ - srcSize && east >= destX &&
                    (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
                ) {
                    return true
                }
            }
            2 -> {
                if (
                    x == destX - srcSize && z <= destZ && north >= destZ &&
                    (flags[east, destZ, level] and CollisionFlag.WALL_EAST) == 0
                ) {
                    return true
                } else if (
                    x <= destX && z == destZ + 1 && east >= destX &&
                    (flags[destX, z, level] and CollisionFlag.WALL_SOUTH) == 0
                ) {
                    return true
                }
            }
            3 -> {
                if (
                    x == destX + 1 && z <= destZ && north >= destZ &&
                    (flags[x, destZ, level] and CollisionFlag.WALL_WEST) == 0
                ) {
                    return true
                } else if (
                    x <= destX && z == destZ + 1 && east >= destX &&
                    (flags[destX, z, level] and CollisionFlag.WALL_SOUTH) == 0
                ) {
                    return true
                }
            }
        }
    } else if (shape == 8) {
        if (
            x <= destX && z == destZ + 1 && east >= destX &&
            (flags[destX, z, level] and CollisionFlag.WALL_SOUTH) == 0
        ) {
            return true
        } else if (
            x <= destX && z == destZ - srcSize && east >= destX &&
            (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
        ) {
            return true
        } else if (
            x == destX - srcSize && z <= destZ && north >= destZ &&
            (flags[east, destZ, level] and CollisionFlag.WALL_EAST) == 0
        ) {
            return true
        }
        return x == destX + 1 && z <= destZ && north >= destZ &&
            (flags[x, destZ, level] and CollisionFlag.WALL_WEST) == 0
    }
    return false
}

private fun Int.alteredRotation(shape: Int): Int {
    return if (shape == 7) (this + 2) and 0x3 else this
}
