package org.rsmod.pathfinder.reach

import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.pathfinder.flag.CollisionFlag
import org.rsmod.pathfinder.util.Rotations

public object ReachStrategy {
    private const val WALL_STRATEGY = 0
    private const val WALL_DECO_STRATEGY = 1
    private const val RECTANGLE_STRATEGY = 2
    private const val NO_STRATEGY = 3
    private const val RECTANGLE_EXCLUSIVE_STRATEGY = 4

    private const val WALLDECOR_DIAGONAL_NOOFFSET_SHAPE = 7

    /**
     * Returns true if coordinates ([srcX], [srcZ]) can reach coordinates ([destX], [destZ]), taking
     * into account the dimensions [destWidth], [destLength] and [srcSize].
     *
     * @param destWidth the _absolute_ width of the destination. This value should _not_ be changed
     *   when passing the width of a rotated loc. (it is done for us within the function)
     * @param destLength the _absolute_ length of the destination. Similar to [destWidth], this
     *   value should _not_ be changed or altered for rotated locs.
     * @param locAngle the angle of the target loc being used as the destination. If the path is
     *   meant for something that is _not_ a loc, this value should be passed or left as the
     *   default 0.
     * @param locShape the shape of the target loc being used as the destination. If the path is
     *   meant for something that is _not_ a loc, this value should be passed or left as the default
     *   -1.
     * @param blockAccessFlags packed directional bitflags where interaction should be blocked. This
     *   can be seen in locs such as staircases, where all directions excluding the direction with
     *   access to the steps are "blocked." (see [org.rsmod.pathfinder.flag.BlockAccessFlag])
     */
    public fun reached(
        flags: CollisionFlagMap,
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        destWidth: Int,
        destLength: Int,
        srcSize: Int,
        locAngle: Int = 0,
        locShape: Int = -1,
        blockAccessFlags: Int = 0,
    ): Boolean {
        val exitStrategy = exitStrategy(locShape)
        if (exitStrategy != RECTANGLE_EXCLUSIVE_STRATEGY && srcX == destX && srcZ == destZ)
            return true
        return when (exitStrategy) {
            WALL_STRATEGY ->
                reachWall(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    srcSize = srcSize,
                    locShape = locShape,
                    locAngle = locAngle,
                )
            WALL_DECO_STRATEGY ->
                reachWallDeco(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    srcSize = srcSize,
                    locShape = locShape,
                    locAngle = locAngle,
                )
            RECTANGLE_STRATEGY ->
                reachRectangle(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    srcSize = srcSize,
                    destWidth = destWidth,
                    destLength = destLength,
                    locAngle = locAngle,
                    blockAccessFlags = blockAccessFlags,
                )
            RECTANGLE_EXCLUSIVE_STRATEGY ->
                reachExclusiveRectangle(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    srcSize = srcSize,
                    destWidth = destWidth,
                    destLength = destLength,
                    locAngle = locAngle,
                    blockAccessFlags = blockAccessFlags,
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
        destLength: Int,
        locAngle: Int = 0,
        blockAccessFlags: Int = 0,
    ): Boolean =
        with(RectangularBounds) {
            val rotatedWidth = Rotations.rotate(locAngle, destWidth, destLength)
            val rotatedLength = Rotations.rotate(locAngle, destLength, destWidth)
            val rotatedBlockAccess = Rotations.rotate(locAngle, blockAccessFlags)
            return if (srcSize > 1) {
                collides(srcX, srcZ, destX, destZ, srcSize, srcSize, rotatedWidth, rotatedLength) ||
                    reachRectangleN(
                        flags = flags,
                        level = level,
                        srcX = srcX,
                        srcZ = srcZ,
                        destX = destX,
                        destZ = destZ,
                        srcWidth = srcSize,
                        srcLength = srcSize,
                        destWidth = rotatedWidth,
                        destLength = rotatedLength,
                        blockAccessFlags = rotatedBlockAccess,
                    )
            } else {
                collides(srcX, srcZ, destX, destZ, srcSize, srcSize, rotatedWidth, rotatedLength) ||
                    reachRectangle1(
                        flags = flags,
                        level = level,
                        srcX = srcX,
                        srcZ = srcZ,
                        destX = destX,
                        destZ = destZ,
                        destWidth = rotatedWidth,
                        destLength = rotatedLength,
                        blockAccessFlags = rotatedBlockAccess,
                    )
            }
        }

    /** @author Kris | 12/09/2021 */
    public fun reachExclusiveRectangle(
        flags: CollisionFlagMap,
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcSize: Int,
        destWidth: Int,
        destLength: Int,
        locAngle: Int = 0,
        blockAccessFlags: Int = 0,
    ): Boolean =
        with(RectangularBounds) {
            val rotatedWidth = Rotations.rotate(locAngle, destWidth, destLength)
            val rotatedLength = Rotations.rotate(locAngle, destLength, destWidth)
            val rotatedBlockAccess = Rotations.rotate(locAngle, blockAccessFlags)
            return if (srcSize > 1) {
                !collides(
                    srcX,
                    srcZ,
                    destX,
                    destZ,
                    srcSize,
                    srcSize,
                    rotatedWidth,
                    rotatedLength,
                ) &&
                    reachRectangleN(
                        flags = flags,
                        level = level,
                        srcX = srcX,
                        srcZ = srcZ,
                        destX = destX,
                        destZ = destZ,
                        srcWidth = srcSize,
                        srcLength = srcSize,
                        destWidth = rotatedWidth,
                        destLength = rotatedLength,
                        blockAccessFlags = rotatedBlockAccess,
                    )
            } else {
                !collides(
                    srcX,
                    srcZ,
                    destX,
                    destZ,
                    srcSize,
                    srcSize,
                    rotatedWidth,
                    rotatedLength,
                ) &&
                    reachRectangle1(
                        flags = flags,
                        level = level,
                        srcX = srcX,
                        srcZ = srcZ,
                        destX = destX,
                        destZ = destZ,
                        destWidth = rotatedWidth,
                        destLength = rotatedLength,
                        blockAccessFlags = rotatedBlockAccess,
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
        locShape: Int,
        locAngle: Int,
    ): Boolean =
        when {
            srcSize == 1 && srcX == destX && srcZ == destZ -> true
            srcSize != 1 &&
                destX >= srcX &&
                srcSize + srcX - 1 >= destX &&
                destZ >= srcZ &&
                srcSize + srcZ - 1 >= destZ -> true
            srcSize == 1 ->
                reachWall1(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    locShape = locShape,
                    locAngle = locAngle,
                )
            else ->
                reachWallN(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    srcSize = srcSize,
                    locShape = locShape,
                    locAngle = locAngle,
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
        locShape: Int,
        locAngle: Int,
    ): Boolean =
        when {
            srcSize == 1 && srcX == destX && destZ == srcZ -> true
            srcSize != 1 &&
                destX >= srcX &&
                srcSize + srcX - 1 >= destX &&
                destZ >= srcZ &&
                srcSize + srcZ - 1 >= destZ -> true
            srcSize == 1 ->
                reachWallDeco1(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    locShape = locShape,
                    locAngle = locAngle,
                )
            else ->
                reachWallDecoN(
                    flags = flags,
                    level = level,
                    srcX = srcX,
                    srcZ = srcZ,
                    destX = destX,
                    destZ = destZ,
                    srcSize = srcSize,
                    locShape = locShape,
                    locAngle = locAngle,
                )
        }

    public fun reachWall1(
        flags: CollisionFlagMap,
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        locShape: Int,
        locAngle: Int,
    ): Boolean {
        when (locShape) {
            0 -> {
                when (locAngle) {
                    0 -> {
                        if (srcX == destX - 1 && srcZ == destZ) {
                            return true
                        } else if (
                            srcX == destX &&
                                srcZ == destZ + 1 &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                        ) {
                            return true
                        } else if (
                            srcX == destX &&
                                srcZ == destZ - 1 &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_SOUTH) == 0
                        ) {
                            return true
                        }
                    }
                    1 -> {
                        if (srcX == destX && srcZ == destZ + 1) {
                            return true
                        } else if (
                            srcX == destX - 1 &&
                                srcZ == destZ &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_WEST) == 0
                        ) {
                            return true
                        } else if (
                            srcX == destX + 1 &&
                                srcZ == destZ &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_EAST) == 0
                        ) {
                            return true
                        }
                    }
                    2 -> {
                        if (srcX == destX + 1 && srcZ == destZ) {
                            return true
                        } else if (
                            srcX == destX &&
                                srcZ == destZ + 1 &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                        ) {
                            return true
                        } else if (
                            srcX == destX &&
                                srcZ == destZ - 1 &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_SOUTH) == 0
                        ) {
                            return true
                        }
                    }
                    3 -> {
                        if (srcX == destX && srcZ == destZ - 1) {
                            return true
                        } else if (
                            srcX == destX - 1 &&
                                srcZ == destZ &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_WEST) == 0
                        ) {
                            return true
                        } else if (
                            srcX == destX + 1 &&
                                srcZ == destZ &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_EAST) == 0
                        ) {
                            return true
                        }
                    }
                }
            }
            2 -> {
                when (locAngle) {
                    0 -> {
                        if (srcX == destX - 1 && srcZ == destZ) {
                            return true
                        } else if (srcX == destX && srcZ == destZ + 1) {
                            return true
                        } else if (
                            srcX == destX + 1 &&
                                srcZ == destZ &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_EAST) == 0
                        ) {
                            return true
                        } else if (
                            srcX == destX &&
                                srcZ == destZ - 1 &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_SOUTH) == 0
                        ) {
                            return true
                        }
                    }
                    1 -> {
                        if (
                            srcX == destX - 1 &&
                                srcZ == destZ &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_WEST) == 0
                        ) {
                            return true
                        } else if (srcX == destX && srcZ == destZ + 1) {
                            return true
                        } else if (srcX == destX + 1 && srcZ == destZ) {
                            return true
                        } else if (
                            srcX == destX &&
                                srcZ == destZ - 1 &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_SOUTH) == 0
                        ) {
                            return true
                        }
                    }
                    2 -> {
                        if (
                            srcX == destX - 1 &&
                                srcZ == destZ &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_WEST) == 0
                        ) {
                            return true
                        } else if (
                            srcX == destX &&
                                srcZ == destZ + 1 &&
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
                            srcX == destX &&
                                srcZ == destZ + 1 &&
                                (flags[srcX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                        ) {
                            return true
                        } else if (
                            srcX == destX + 1 &&
                                srcZ == destZ &&
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
                    srcX == destX &&
                        srcZ == destZ + 1 &&
                        (flags[srcX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
                ) {
                    return true
                } else if (
                    srcX == destX &&
                        srcZ == destZ - 1 &&
                        (flags[srcX, srcZ, level] and CollisionFlag.WALL_NORTH) == 0
                ) {
                    return true
                } else if (
                    srcX == destX - 1 &&
                        srcZ == destZ &&
                        (flags[srcX, srcZ, level] and CollisionFlag.WALL_EAST) == 0
                ) {
                    return true
                }
                return srcX == destX + 1 &&
                    srcZ == destZ &&
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
        locShape: Int,
        locAngle: Int,
    ): Boolean {
        val east = srcX + srcSize - 1
        val north = srcZ + srcSize - 1
        when (locShape) {
            0 -> {
                when (locAngle) {
                    0 -> {
                        if (srcX == destX - srcSize && srcZ <= destZ && north >= destZ) {
                            return true
                        } else if (
                            destX in srcX..east &&
                                srcZ == destZ + 1 &&
                                (flags[destX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                        ) {
                            return true
                        } else if (
                            destX in srcX..east &&
                                srcZ == destZ - srcSize &&
                                (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                        ) {
                            return true
                        }
                    }
                    1 -> {
                        if (destX in srcX..east && srcZ == destZ + 1) {
                            return true
                        } else if (
                            srcX == destX - srcSize &&
                                srcZ <= destZ &&
                                north >= destZ &&
                                (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                        ) {
                            return true
                        } else if (
                            srcX == destX + 1 &&
                                srcZ <= destZ &&
                                north >= destZ &&
                                (flags[srcX, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
                        ) {
                            return true
                        }
                    }
                    2 -> {
                        if (srcX == destX + 1 && srcZ <= destZ && north >= destZ) {
                            return true
                        } else if (
                            destX in srcX..east &&
                                srcZ == destZ + 1 &&
                                (flags[destX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                        ) {
                            return true
                        } else if (
                            destX in srcX..east &&
                                srcZ == destZ - srcSize &&
                                (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                        ) {
                            return true
                        }
                    }
                    3 -> {
                        if (destX in srcX..east && srcZ == destZ - srcSize) {
                            return true
                        } else if (
                            srcX == destX - srcSize &&
                                srcZ <= destZ &&
                                north >= destZ &&
                                (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                        ) {
                            return true
                        } else if (
                            srcX == destX + 1 &&
                                srcZ <= destZ &&
                                north >= destZ &&
                                (flags[srcX, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
                        ) {
                            return true
                        }
                    }
                }
            }
            2 -> {
                when (locAngle) {
                    0 -> {
                        if (srcX == destX - srcSize && srcZ <= destZ && north >= destZ) {
                            return true
                        } else if (destX in srcX..east && srcZ == destZ + 1) {
                            return true
                        } else if (
                            srcX == destX + 1 &&
                                srcZ <= destZ &&
                                north >= destZ &&
                                (flags[srcX, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
                        ) {
                            return true
                        } else if (
                            destX in srcX..east &&
                                srcZ == destZ - srcSize &&
                                (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                        ) {
                            return true
                        }
                    }
                    1 -> {
                        if (
                            srcX == destX - srcSize &&
                                srcZ <= destZ &&
                                north >= destZ &&
                                (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                        ) {
                            return true
                        } else if (destX in srcX..east && srcZ == destZ + 1) {
                            return true
                        } else if (srcX == destX + 1 && srcZ <= destZ && north >= destZ) {
                            return true
                        } else if (
                            destX in srcX..east &&
                                srcZ == destZ - srcSize &&
                                (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                        ) {
                            return true
                        }
                    }
                    2 -> {
                        if (
                            srcX == destX - srcSize &&
                                srcZ <= destZ &&
                                north >= destZ &&
                                (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                        ) {
                            return true
                        } else if (
                            destX in srcX..east &&
                                srcZ == destZ + 1 &&
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
                            destX in srcX..east &&
                                srcZ == destZ + 1 &&
                                (flags[destX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                        ) {
                            return true
                        } else if (
                            srcX == destX + 1 &&
                                srcZ <= destZ &&
                                north >= destZ &&
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
                    destX in srcX..east &&
                        srcZ == destZ + 1 &&
                        (flags[destX, srcZ, level] and CollisionFlag.BLOCK_NORTH) == 0
                ) {
                    return true
                } else if (
                    destX in srcX..east &&
                        srcZ == destZ - srcSize &&
                        (flags[destX, north, level] and CollisionFlag.BLOCK_SOUTH) == 0
                ) {
                    return true
                } else if (
                    srcX == destX - srcSize &&
                        srcZ <= destZ &&
                        north >= destZ &&
                        (flags[east, destZ, level] and CollisionFlag.BLOCK_WEST) == 0
                ) {
                    return true
                }
                return srcX == destX + 1 &&
                    srcZ <= destZ &&
                    north >= destZ &&
                    (flags[srcX, destZ, level] and CollisionFlag.BLOCK_EAST) == 0
            }
        }
        return false
    }

    private fun reachWallDeco1(
        flags: CollisionFlagMap,
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        locShape: Int,
        locAngle: Int,
    ): Boolean {
        if (locShape in 6..7) {
            when (locAngle.alteredAngle(locShape)) {
                0 -> {
                    if (
                        srcX == destX + 1 &&
                            srcZ == destZ &&
                            (flags[srcX, srcZ, level] and CollisionFlag.WALL_WEST) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX &&
                            srcZ == destZ - 1 &&
                            (flags[srcX, srcZ, level] and CollisionFlag.WALL_NORTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (
                        srcX == destX - 1 &&
                            srcZ == destZ &&
                            (flags[srcX, srcZ, level] and CollisionFlag.WALL_EAST) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX &&
                            srcZ == destZ - 1 &&
                            (flags[srcX, srcZ, level] and CollisionFlag.WALL_NORTH) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (
                        srcX == destX - 1 &&
                            srcZ == destZ &&
                            (flags[srcX, srcZ, level] and CollisionFlag.WALL_EAST) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX &&
                            srcZ == destZ + 1 &&
                            (flags[srcX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                3 -> {
                    if (
                        srcX == destX + 1 &&
                            srcZ == destZ &&
                            (flags[srcX, srcZ, level] and CollisionFlag.WALL_WEST) == 0
                    ) {
                        return true
                    } else if (
                        srcX == destX &&
                            srcZ == destZ + 1 &&
                            (flags[srcX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
                    ) {
                        return true
                    }
                }
            }
        } else if (locShape == 8) {
            if (
                srcX == destX &&
                    srcZ == destZ + 1 &&
                    (flags[srcX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
            ) {
                return true
            } else if (
                srcX == destX &&
                    srcZ == destZ - 1 &&
                    (flags[srcX, srcZ, level] and CollisionFlag.WALL_NORTH) == 0
            ) {
                return true
            } else if (
                srcX == destX - 1 &&
                    srcZ == destZ &&
                    (flags[srcX, srcZ, level] and CollisionFlag.WALL_EAST) == 0
            ) {
                return true
            }
            return srcX == destX + 1 &&
                srcZ == destZ &&
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
        locShape: Int,
        locAngle: Int,
    ): Boolean {
        val east = srcX + srcSize - 1
        val north = srcZ + srcSize - 1
        if (locShape in 6..7) {
            when (locAngle.alteredAngle(locShape)) {
                0 -> {
                    if (
                        srcX == destX + 1 &&
                            srcZ <= destZ &&
                            north >= destZ &&
                            (flags[srcX, destZ, level] and CollisionFlag.WALL_WEST) == 0
                    ) {
                        return true
                    } else if (
                        srcX <= destX &&
                            srcZ == destZ - srcSize &&
                            east >= destX &&
                            (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
                    ) {
                        return true
                    }
                }
                1 -> {
                    if (
                        srcX == destX - srcSize &&
                            srcZ <= destZ &&
                            north >= destZ &&
                            (flags[east, destZ, level] and CollisionFlag.WALL_EAST) == 0
                    ) {
                        return true
                    } else if (
                        srcX <= destX &&
                            srcZ == destZ - srcSize &&
                            east >= destX &&
                            (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
                    ) {
                        return true
                    }
                }
                2 -> {
                    if (
                        srcX == destX - srcSize &&
                            srcZ <= destZ &&
                            north >= destZ &&
                            (flags[east, destZ, level] and CollisionFlag.WALL_EAST) == 0
                    ) {
                        return true
                    } else if (
                        srcX <= destX &&
                            srcZ == destZ + 1 &&
                            east >= destX &&
                            (flags[destX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
                    ) {
                        return true
                    }
                }
                3 -> {
                    if (
                        srcX == destX + 1 &&
                            srcZ <= destZ &&
                            north >= destZ &&
                            (flags[srcX, destZ, level] and CollisionFlag.WALL_WEST) == 0
                    ) {
                        return true
                    } else if (
                        srcX <= destX &&
                            srcZ == destZ + 1 &&
                            east >= destX &&
                            (flags[destX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
                    ) {
                        return true
                    }
                }
            }
        } else if (locShape == 8) {
            if (
                srcX <= destX &&
                    srcZ == destZ + 1 &&
                    east >= destX &&
                    (flags[destX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0
            ) {
                return true
            } else if (
                srcX <= destX &&
                    srcZ == destZ - srcSize &&
                    east >= destX &&
                    (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
            ) {
                return true
            } else if (
                srcX == destX - srcSize &&
                    srcZ <= destZ &&
                    north >= destZ &&
                    (flags[east, destZ, level] and CollisionFlag.WALL_EAST) == 0
            ) {
                return true
            }
            return srcX == destX + 1 &&
                srcZ <= destZ &&
                north >= destZ &&
                (flags[srcX, destZ, level] and CollisionFlag.WALL_WEST) == 0
        }
        return false
    }

    private fun exitStrategy(locShape: Int): Int =
        when {
            locShape == -2 -> RECTANGLE_EXCLUSIVE_STRATEGY
            locShape == -1 -> NO_STRATEGY
            locShape in 0..3 || locShape == 9 -> WALL_STRATEGY
            locShape < 9 -> WALL_DECO_STRATEGY
            locShape in 10..11 || locShape == 22 -> RECTANGLE_STRATEGY
            else -> NO_STRATEGY
        }

    private fun Int.alteredAngle(shape: Int): Int {
        return if (shape == WALLDECOR_DIAGONAL_NOOFFSET_SHAPE) {
            (this + 2) and 0x3
        } else {
            this
        }
    }
}
