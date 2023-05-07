package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag
import org.rsmod.game.pathfinder.util.RotationUtils.rotate

@Suppress("MemberVisibilityCanBePrivate")
public object ReachStrategy {

    private const val WALL_STRATEGY = 0
    private const val WALL_DECO_STRATEGY = 1
    private const val RECTANGLE_STRATEGY = 2
    private const val NO_STRATEGY = 3
    private const val RECTANGLE_EXCLUSIVE_STRATEGY = 4

    /**
     * Returns true if coordinates ([srcX], [srcZ]) can reach coordinates ([destX], [destZ]),
     * taking into account the dimensions [destWidth], [destHeight] and [srcSize].
     *
     * @param destWidth the _absolute_ width of the destination. This value should _not_ be
     * changed when passing the width of a rotated object. (it is done for us within the
     * function)
     *
     * @param destHeight the _absolute_ height of the destination. Similar to [destWidth],
     * this value should _not_ be changed or altered for rotated objects.
     *
     * @param objRot the rotation of the target object being used as the destination. If
     * the path is meant for something that is _not_ a game object, this value should be
     * passed or left as the default 0.
     *
     * @param objShape the shape of the target object being used as the destination. If
     * the path is meant for something that is _not_ a game object, this value should be
     * passed or left as the default -1.
     *
     * @param blockAccessFlags packed directional bitflags where interaction should be
     * blocked. This can be seen in game objects such as staircases, where all directions
     * excluding the direction with access to the steps are "blocked."
     * (see [org.rsmod.game.pathfinder.flag.BlockAccessFlag])
     */
    public fun reached(
        flags: CollisionFlagMap,
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        destWidth: Int,
        destHeight: Int,
        srcSize: Int,
        objRot: Int = 0,
        objShape: Int = -1,
        blockAccessFlags: Int = 0
    ): Boolean {
        val exitStrategy = exitStrategy(objShape)
        if (exitStrategy != RECTANGLE_EXCLUSIVE_STRATEGY && srcX == destX && srcZ == destZ) return true
        return when (exitStrategy) {
            WALL_STRATEGY -> reachWall(
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
            WALL_DECO_STRATEGY -> reachWallDeco(
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
            RECTANGLE_STRATEGY -> reachRectangle(
                flags = flags,
                level = level,
                srcX = srcX,
                srcZ = srcZ,
                destX = destX,
                destZ = destZ,
                srcSize = srcSize,
                destWidth = destWidth,
                destHeight = destHeight,
                objRot = objRot,
                blockAccessFlags = blockAccessFlags
            )
            RECTANGLE_EXCLUSIVE_STRATEGY -> reachExclusiveRectangle(
                flags = flags,
                level = level,
                srcX = srcX,
                srcZ = srcZ,
                destX = destX,
                destZ = destZ,
                srcSize = srcSize,
                destWidth = destWidth,
                destHeight = destHeight,
                objRot = objRot,
                blockAccessFlags = blockAccessFlags
            )
            else -> false
        }
    }

    public fun reachRectangle(
        flags: CollisionFlagMap,
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcSize: Int,
        destWidth: Int,
        destHeight: Int,
        objRot: Int = 0,
        blockAccessFlags: Int = 0
    ): Boolean = with(RectangleBoundaryUtils) {
        val rotatedWidth = rotate(objRot, destWidth, destHeight)
        val rotatedHeight = rotate(objRot, destHeight, destWidth)
        val rotatedBlockAccess = rotate(objRot, blockAccessFlags)
        return if (srcSize > 1) {
            collides(srcX, srcZ, destX, destZ, srcSize, srcSize, rotatedWidth, rotatedHeight) ||
                reachRectangleN(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    srcWidth = srcSize,
                    srcHeight = srcSize,
                    destWidth = rotatedWidth,
                    destHeight = rotatedHeight,
                    blockAccessFlags = rotatedBlockAccess
                )
        } else {
            collides(srcX, srcZ, destX, destZ, srcSize, srcSize, rotatedWidth, rotatedHeight) ||
                reachRectangle1(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    destWidth = rotatedWidth,
                    destHeight = rotatedHeight,
                    blockAccessFlags = rotatedBlockAccess
                )
        }
    }

    /**
     * @author Kris | 12/09/2021
     */
    public fun reachExclusiveRectangle(
        flags: CollisionFlagMap,
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcSize: Int,
        destWidth: Int,
        destHeight: Int,
        objRot: Int = 0,
        blockAccessFlags: Int = 0
    ): Boolean = with(RectangleBoundaryUtils) {
        val rotatedWidth = rotate(objRot, destWidth, destHeight)
        val rotatedHeight = rotate(objRot, destHeight, destWidth)
        val rotatedBlockAccess = rotate(objRot, blockAccessFlags)
        return if (srcSize > 1) {
            !collides(srcX, srcZ, destX, destZ, srcSize, srcSize, rotatedWidth, rotatedHeight) &&
                reachRectangleN(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    srcWidth = srcSize,
                    srcHeight = srcSize,
                    destWidth = rotatedWidth,
                    destHeight = rotatedHeight,
                    blockAccessFlags = rotatedBlockAccess
                )
        } else {
            !collides(srcX, srcZ, destX, destZ, srcSize, srcSize, rotatedWidth, rotatedHeight) &&
                reachRectangle1(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    destWidth = rotatedWidth,
                    destHeight = rotatedHeight,
                    blockAccessFlags = rotatedBlockAccess
                )
        }
    }

    public fun reachWall(
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

    public fun reachWallDeco(
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

    public fun reachWall1(
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

    private fun exitStrategy(objShape: Int): Int = when {
        objShape == -2 -> RECTANGLE_EXCLUSIVE_STRATEGY
        objShape == -1 -> NO_STRATEGY
        objShape in 0..3 || objShape == 9 -> WALL_STRATEGY
        objShape < 9 -> WALL_DECO_STRATEGY
        objShape in 10..11 || objShape == 22 -> RECTANGLE_STRATEGY
        else -> NO_STRATEGY
    }

    private fun Int.alteredRotation(shape: Int): Int {
        return if (shape == 7) (this + 2) and 0x3 else this
    }
}
