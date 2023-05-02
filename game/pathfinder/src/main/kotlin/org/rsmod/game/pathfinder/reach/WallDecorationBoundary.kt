package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag

internal fun reachWallDeco(
    flags: CollisionFlagMap,
    level: Int,
    srcX: Int,
    srcZ: Int,
    destX: Int,
    destZ: Int,
    srcSize: Int,
    objShape: Int,
    objRot: Int
): Boolean = when {
    srcSize == 1 && srcX == destX && destZ == srcZ -> true
    srcSize != 1 && destX >= srcX && srcSize + srcX - 1 >= destX &&
        destZ >= srcZ && srcSize + srcZ - 1 >= destZ -> true
    srcSize == 1 -> reachWallDeco1(
        flags = flags,
        level = level,
        srcX = srcX,
        srcZ = srcZ,
        destX = destX,
        destZ = destZ,
        objShape = objShape,
        objRot = objRot
    )
    else -> reachWallDecoN(
        flags = flags,
        level = level,
        srcX = srcX,
        srcZ = srcZ,
        destX = destX,
        destZ = destZ,
        srcSize = srcSize,
        objShape = objShape,
        objRot = objRot
    )
}

@Suppress("DuplicatedCode")
private fun reachWallDeco1(
    flags: CollisionFlagMap,
    level: Int,
    srcX: Int,
    srcZ: Int,
    destX: Int,
    destZ: Int,
    objShape: Int,
    objRot: Int
): Boolean {
    if (objShape in 6..7) {
        when (objRot.alteredRotation(objShape)) {
            0 -> {
                if (
                    srcX == destX + 1 && srcZ == destZ &&
                    (flags[srcX, srcZ, level] and CollisionFlag.WALL_WEST) == 0
                ) {
                    return true
                } else if (
                    srcX == destX && srcZ == destZ - 1 &&
                    (flags[srcX, srcZ, level] and CollisionFlag.WALL_NORTH) == 0
                ) {
                    return true
                }
            }
            1 -> {
                if (
                    srcX == destX - 1 && srcZ == destZ &&
                    (flags[srcX, srcZ, level] and CollisionFlag.WALL_EAST) == 0
                ) {
                    return true
                } else if (
                    srcX == destX && srcZ == destZ - 1 &&
                    (flags[srcX, srcZ, level] and CollisionFlag.WALL_NORTH) == 0
                ) {
                    return true
                }
            }
            2 -> {
                if (
                    srcX == destX - 1 && srcZ == destZ &&
                    (flags[srcX, srcZ, level] and CollisionFlag.WALL_EAST) == 0
                ) {
                    return true
                } else if (
                    srcX == destX && srcZ == destZ + 1 &&
                    (flags[srcX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
                ) {
                    return true
                }
            }
            3 -> {
                if (
                    srcX == destX + 1 && srcZ == destZ &&
                    (flags[srcX, srcZ, level] and CollisionFlag.WALL_WEST) == 0
                ) {
                    return true
                } else if (
                    srcX == destX && srcZ == destZ + 1 &&
                    (flags[srcX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
                ) {
                    return true
                }
            }
        }
    } else if (objShape == 8) {
        if (
            srcX == destX && srcZ == destZ + 1 &&
            (flags[srcX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
        ) {
            return true
        } else if (
            srcX == destX && srcZ == destZ - 1 &&
            (flags[srcX, srcZ, level] and CollisionFlag.WALL_NORTH) == 0
        ) {
            return true
        } else if (
            srcX == destX - 1 && srcZ == destZ &&
            (flags[srcX, srcZ, level] and CollisionFlag.WALL_EAST) == 0
        ) {
            return true
        }
        return srcX == destX + 1 && srcZ == destZ &&
            (flags[srcX, srcZ, level] and CollisionFlag.WALL_WEST) == 0
    }
    return false
}

private fun reachWallDecoN(
    flags: CollisionFlagMap,
    level: Int,
    srcX: Int,
    srcZ: Int,
    destX: Int,
    destZ: Int,
    srcSize: Int,
    objShape: Int,
    objRot: Int
): Boolean {
    val east = srcX + srcSize - 1
    val north = srcZ + srcSize - 1
    if (objShape in 6..7) {
        when (objRot.alteredRotation(objShape)) {
            0 -> {
                if (
                    srcX == destX + 1 && srcZ <= destZ && north >= destZ &&
                    (flags[srcX, destZ, level] and CollisionFlag.WALL_WEST) == 0
                ) {
                    return true
                } else if (srcX <= destX && srcZ == destZ - srcSize && east >= destX &&
                    (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
                ) {
                    return true
                }
            }
            1 -> {
                if (
                    srcX == destX - srcSize && srcZ <= destZ && north >= destZ &&
                    (flags[east, destZ, level] and CollisionFlag.WALL_EAST) == 0
                ) {
                    return true
                } else if (
                    srcX <= destX && srcZ == destZ - srcSize && east >= destX &&
                    (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
                ) {
                    return true
                }
            }
            2 -> {
                if (
                    srcX == destX - srcSize && srcZ <= destZ && north >= destZ &&
                    (flags[east, destZ, level] and CollisionFlag.WALL_EAST) == 0
                ) {
                    return true
                } else if (
                    srcX <= destX && srcZ == destZ + 1 && east >= destX &&
                    (flags[destX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
                ) {
                    return true
                }
            }
            3 -> {
                if (
                    srcX == destX + 1 && srcZ <= destZ && north >= destZ &&
                    (flags[srcX, destZ, level] and CollisionFlag.WALL_WEST) == 0
                ) {
                    return true
                } else if (
                    srcX <= destX && srcZ == destZ + 1 && east >= destX &&
                    (flags[destX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
                ) {
                    return true
                }
            }
        }
    } else if (objShape == 8) {
        if (
            srcX <= destX && srcZ == destZ + 1 && east >= destX &&
            (flags[destX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
        ) {
            return true
        } else if (
            srcX <= destX && srcZ == destZ - srcSize && east >= destX &&
            (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
        ) {
            return true
        } else if (
            srcX == destX - srcSize && srcZ <= destZ && north >= destZ &&
            (flags[east, destZ, level] and CollisionFlag.WALL_EAST) == 0
        ) {
            return true
        }
        return srcX == destX + 1 && srcZ <= destZ && north >= destZ &&
            (flags[srcX, destZ, level] and CollisionFlag.WALL_WEST) == 0
    }
    return false
}

private fun Int.alteredRotation(shape: Int): Int {
    return if (shape == 7) (this + 2) and 0x3 else this
}
