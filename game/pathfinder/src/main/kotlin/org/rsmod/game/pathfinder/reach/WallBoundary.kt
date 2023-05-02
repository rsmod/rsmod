@file:Suppress("DuplicatedCode")

package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag

internal fun reachWall(
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
    srcSize == 1 && srcX == destX && srcZ == destZ -> true
    srcSize != 1 && destX >= srcX && srcSize + srcX - 1 >= destX &&
        destZ >= srcZ && srcSize + srcZ - 1 >= destZ -> true
    srcSize == 1 -> reachWall1(
        flags = flags,
        level = level,
        srcX = srcX,
        srcZ = srcZ,
        destX = destX,
        destZ = destZ,
        objShape = objShape,
        objRot = objRot
    )
    else -> reachWallN(
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

private fun reachWall1(
    flags: CollisionFlagMap,
    level: Int,
    srcX: Int,
    srcZ: Int,
    destX: Int,
    destZ: Int,
    objShape: Int,
    objRot: Int
): Boolean {
    when (objShape) {
        0 -> {
            when (objRot) {
                0 -> {
                    if (srcX == destX - 1 && srcZ == destZ) {
                        return true
                    } else if (
                        srcX == destX && srcZ == destZ + 1 &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX && srcZ == destZ - 1 &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (srcX == destX && srcZ == destZ + 1) {
                        return true
                    } else if (
                        srcX == destX - 1 && srcZ == destZ &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX + 1 && srcZ == destZ &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (srcX == destX + 1 && srcZ == destZ) {
                        return true
                    } else if (
                        srcX == destX && srcZ == destZ + 1 &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX && srcZ == destZ - 1 &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                3 -> {
                    if (srcX == destX && srcZ == destZ - 1) {
                        return true
                    } else if (
                        srcX == destX - 1 && srcZ == destZ &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX + 1 && srcZ == destZ &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
            }
        }
        2 -> {
            when (objRot) {
                0 -> {
                    if (srcX == destX - 1 && srcZ == destZ) {
                        return true
                    } else if (srcX == destX && srcZ == destZ + 1) {
                        return true
                    } else if (
                        srcX == destX + 1 && srcZ == destZ &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX && srcZ == destZ - 1 &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (
                        srcX == destX - 1 && srcZ == destZ &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (srcX == destX && srcZ == destZ + 1) {
                        return true
                    } else if (srcX == destX + 1 && srcZ == destZ) {
                        return true
                    } else if (
                        srcX == destX && srcZ == destZ - 1 &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (
                        srcX == destX - 1 && srcZ == destZ &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX && srcZ == destZ + 1 &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (srcX == destX + 1 && srcZ == destZ) {
                        return true
                    } else if (srcX == destX && srcZ == destZ - 1) {
                        return true
                    }
                }
                3 -> {
                    if (srcX == destX - 1 && srcZ == destZ) {
                        return true
                    } else if (
                        srcX == destX && srcZ == destZ + 1 &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX + 1 && srcZ == destZ &&
                        (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (srcX == destX && srcZ == destZ - 1) {
                        return true
                    }
                }
            }
        }
        9 -> {
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
    }
    return false
}

private fun reachWallN(
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
    when (objShape) {
        0 -> {
            when (objRot) {
                0 -> {
                    if (srcX == destX - srcSize && srcZ <= destZ && north >= destZ) {
                        return true
                    } else if (
                        destX in srcX..east && srcZ == destZ + 1 &&
                        (flags[destX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (destX in srcX..east && srcZ == destZ - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (destX in srcX..east && srcZ == destZ + 1) {
                        return true
                    } else if (
                        srcX == destX - srcSize && srcZ <= destZ && north >= destZ &&
                        (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (srcX == destX + 1 && srcZ <= destZ && north >= destZ &&
                        (flags[srcX, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (srcX == destX + 1 && srcZ <= destZ && north >= destZ) {
                        return true
                    } else if (
                        destX in srcX..east && srcZ == destZ + 1 &&
                        (flags[destX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        destX in srcX..east && srcZ == destZ - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                3 -> {
                    if (destX in srcX..east && srcZ == destZ - srcSize) {
                        return true
                    } else if (
                        srcX == destX - srcSize && srcZ <= destZ && north >= destZ &&
                        (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX + 1 && srcZ <= destZ && north >= destZ &&
                        (flags[srcX, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    }
                }
            }
        }
        2 -> {
            when (objRot) {
                0 -> {
                    if (srcX == destX - srcSize && srcZ <= destZ && north >= destZ) {
                        return true
                    } else if (destX in srcX..east && srcZ == destZ + 1) {
                        return true
                    } else if (
                        srcX == destX + 1 && srcZ <= destZ && north >= destZ &&
                        (flags[srcX, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (destX in srcX..east && srcZ == destZ - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (
                        srcX == destX - srcSize && srcZ <= destZ && north >= destZ &&
                        (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (destX in srcX..east && srcZ == destZ + 1) {
                        return true
                    } else if (srcX == destX + 1 && srcZ <= destZ && north >= destZ) {
                        return true
                    } else if (
                        destX in srcX..east && srcZ == destZ - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (
                        srcX == destX - srcSize && srcZ <= destZ && north >= destZ &&
                        (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                    ) {
                        return true
                    } else if (
                        destX in srcX..east && srcZ == destZ + 1 &&
                        (flags[destX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (srcX == destX + 1 && srcZ <= destZ && north >= destZ) {
                        return true
                    } else if (destX in srcX..east && srcZ == destZ - srcSize) {
                        return true
                    }
                }
                3 -> {
                    if (srcX == destX - srcSize && srcZ <= destZ && north >= destZ) {
                        return true
                    } else if (
                        destX in srcX..east && srcZ == destZ + 1 &&
                        (flags[destX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX + 1 && srcZ <= destZ && north >= destZ &&
                        (flags[srcX, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
                    ) {
                        return true
                    } else if (destX in srcX..east && srcZ == destZ - srcSize) {
                        return true
                    }
                }
            }
        }
        9 -> {
            if (
                destX in srcX..east && srcZ == destZ + 1 &&
                (flags[destX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
            ) {
                return true
            } else if (
                destX in srcX..east && srcZ == destZ - srcSize &&
                (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
            ) {
                return true
            } else if (
                srcX == destX - srcSize && srcZ <= destZ && north >= destZ &&
                (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
            ) {
                return true
            }
            return srcX == destX + 1 && srcZ <= destZ && north >= destZ &&
                (flags[srcX, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
        }
    }
    return false
}
