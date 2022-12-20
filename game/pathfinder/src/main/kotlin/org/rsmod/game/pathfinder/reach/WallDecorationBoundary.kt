package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag

internal fun reachWallDeco(
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
    srcSize == 1 && x == destX && destY == y -> true
    srcSize != 1 && destX >= x && srcSize + x - 1 >= destX &&
        destY >= y && srcSize + y - 1 >= destY -> true
    srcSize == 1 -> reachWallDeco1(
        flags,
        x,
        y,
        level,
        destX,
        destY,
        shape,
        rot
    )
    else -> reachWallDecoN(
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

@Suppress("DuplicatedCode")
private fun reachWallDeco1(
    flags: CollisionFlagMap,
    x: Int,
    y: Int,
    level: Int,
    destX: Int,
    destY: Int,
    shape: Int,
    rot: Int
): Boolean {
    if (shape in 6..7) {
        when (rot.alteredRotation(shape)) {
            0 -> {
                if (x == destX + 1 && y == destY &&
                    (flags[x, y, level] and CollisionFlag.WALL_WEST) == 0
                ) return true
                if (x == destX && y == destY - 1 &&
                    (flags[x, y, level] and CollisionFlag.WALL_NORTH) == 0
                ) return true
            }
            1 -> {
                if (x == destX - 1 && y == destY &&
                    (flags[x, y, level] and CollisionFlag.WALL_EAST) == 0
                ) return true
                if (x == destX && y == destY - 1 &&
                    (flags[x, y, level] and CollisionFlag.WALL_NORTH) == 0
                ) return true
            }
            2 -> {
                if (x == destX - 1 && y == destY &&
                    (flags[x, y, level] and CollisionFlag.WALL_EAST) == 0
                ) return true
                if (x == destX && y == destY + 1 &&
                    (flags[x, y, level] and CollisionFlag.WALL_SOUTH) == 0
                ) return true
            }
            3 -> {
                if (x == destX + 1 && y == destY &&
                    (flags[x, y, level] and CollisionFlag.WALL_WEST) == 0
                ) return true
                if (x == destX && y == destY + 1 &&
                    (flags[x, y, level] and CollisionFlag.WALL_SOUTH) == 0
                ) return true
            }
        }
    } else if (shape == 8) {
        if (x == destX && y == destY + 1 &&
            (flags[x, y, level] and CollisionFlag.WALL_SOUTH) == 0
        ) return true
        if (x == destX && y == destY - 1 &&
            (flags[x, y, level] and CollisionFlag.WALL_NORTH) == 0
        ) return true
        if (x == destX - 1 && y == destY &&
            (flags[x, y, level] and CollisionFlag.WALL_EAST) == 0
        ) return true

        return x == destX + 1 && y == destY &&
            (flags[x, y, level] and CollisionFlag.WALL_WEST) == 0
    }
    return false
}

private fun reachWallDecoN(
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
    if (shape in 6..7) {
        when (rot.alteredRotation(shape)) {
            0 -> {
                if (x == destX + 1 && y <= destY && north >= destY &&
                    (flags[x, destY, level] and CollisionFlag.WALL_WEST) == 0
                ) return true
                if (x <= destX && y == destY - srcSize && east >= destX &&
                    (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
                ) return true
            }
            1 -> {
                if (x == destX - srcSize && y <= destY && north >= destY &&
                    (flags[east, destY, level] and CollisionFlag.WALL_EAST) == 0
                ) return true
                if (x <= destX && y == destY - srcSize && east >= destX &&
                    (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
                ) return true
            }
            2 -> {
                if (x == destX - srcSize && y <= destY && north >= destY &&
                    (flags[east, destY, level] and CollisionFlag.WALL_EAST) == 0
                ) return true
                if (x <= destX && y == destY + 1 && east >= destX &&
                    (flags[destX, y, level] and CollisionFlag.WALL_SOUTH) == 0
                ) return true
            }
            3 -> {
                if (x == destX + 1 && y <= destY && north >= destY &&
                    (flags[x, destY, level] and CollisionFlag.WALL_WEST) == 0
                ) return true
                if (x <= destX && y == destY + 1 && east >= destX &&
                    (flags[destX, y, level] and CollisionFlag.WALL_SOUTH) == 0
                ) return true
            }
        }
    } else if (shape == 8) {
        if (x <= destX && y == destY + 1 && east >= destX &&
            (flags[destX, y, level] and CollisionFlag.WALL_SOUTH) == 0
        ) return true
        if (x <= destX && y == destY - srcSize && east >= destX &&
            (flags[destX, north, level] and CollisionFlag.WALL_NORTH) == 0
        ) return true
        if (x == destX - srcSize && y <= destY && north >= destY &&
            (flags[east, destY, level] and CollisionFlag.WALL_EAST) == 0
        ) return true

        return x == destX + 1 && y <= destY && north >= destY &&
            (flags[x, destY, level] and CollisionFlag.WALL_WEST) == 0
    }
    return false
}

private fun Int.alteredRotation(shape: Int): Int {
    return if (shape == 7) (this + 2) and 0x3 else this
}
