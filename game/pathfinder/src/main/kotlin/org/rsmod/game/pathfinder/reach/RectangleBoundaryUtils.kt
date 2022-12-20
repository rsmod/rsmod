package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.AccessBitFlag
import org.rsmod.game.pathfinder.flag.CollisionFlag
import kotlin.math.max
import kotlin.math.min

/**
 * @author Kris | 12/09/2021
 */
public object RectangleBoundaryUtils {

    public fun collides(
        srcX: Int,
        srcY: Int,
        destX: Int,
        destY: Int,
        srcWidth: Int,
        srcHeight: Int,
        destWidth: Int,
        destHeight: Int
    ): Boolean = if (srcX >= destX + destWidth || srcX + srcWidth <= destX) {
        false
    } else {
        srcY < destY + destHeight && destY < srcHeight + srcY
    }

    internal fun reachRectangle1(
        flags: CollisionFlagMap,
        x: Int,
        y: Int,
        level: Int,
        accessBitMask: Int,
        destX: Int,
        destY: Int,
        destWidth: Int,
        destHeight: Int
    ): Boolean {
        val east = destX + destWidth - 1
        val north = destY + destHeight - 1

        if (x == destX - 1 && y >= destY && y <= north &&
            (flags[x, y, level] and CollisionFlag.WALL_EAST) == 0 &&
            (accessBitMask and AccessBitFlag.BLOCK_WEST) == 0
        ) return true

        if (x == east + 1 && y >= destY && y <= north &&
            (flags[x, y, level] and CollisionFlag.WALL_WEST) == 0 &&
            (accessBitMask and AccessBitFlag.BLOCK_EAST) == 0
        ) return true

        if (y + 1 == destY && x >= destX && x <= east &&
            (flags[x, y, level] and CollisionFlag.WALL_NORTH) == 0 &&
            (accessBitMask and AccessBitFlag.BLOCK_SOUTH) == 0

        ) return true

        return y == north + 1 && x >= destX && x <= east &&
            (flags[x, y, level] and CollisionFlag.WALL_SOUTH) == 0 &&
            (accessBitMask and AccessBitFlag.BLOCK_NORTH) == 0
    }

    internal fun reachRectangleN(
        flags: CollisionFlagMap,
        x: Int,
        y: Int,
        level: Int,
        accessBitMask: Int,
        destX: Int,
        destY: Int,
        srcWidth: Int,
        srcHeight: Int,
        destWidth: Int,
        destHeight: Int
    ): Boolean {
        val srcEast = x + srcWidth
        val srcNorth = srcHeight + y
        val destEast = destWidth + destX
        val destNorth = destHeight + destY
        if (destEast == x && (accessBitMask and AccessBitFlag.BLOCK_EAST) == 0) {
            val fromY = max(y, destY)
            val toY = min(srcNorth, destNorth)
            for (sideY in fromY until toY) {
                if (flags[destEast - 1, sideY, level] and CollisionFlag.WALL_EAST == 0) {
                    return true
                }
            }
        } else if (srcEast == destX && (accessBitMask and AccessBitFlag.BLOCK_WEST) == 0) {
            val fromY = max(y, destY)
            val toY = min(srcNorth, destNorth)
            for (sideY in fromY until toY) {
                if (flags[destX, sideY, level] and CollisionFlag.WALL_WEST == 0) {
                    return true
                }
            }
        } else if (y == destNorth && (accessBitMask and AccessBitFlag.BLOCK_NORTH) == 0) {
            val fromX = max(x, destX)
            val toX = min(srcEast, destEast)
            for (sideX in fromX until toX) {
                if (flags[sideX, destNorth - 1, level] and CollisionFlag.WALL_NORTH == 0) {
                    return true
                }
            }
        } else if (destY == srcNorth && (accessBitMask and AccessBitFlag.BLOCK_SOUTH) == 0) {
            val fromX = max(x, destX)
            val toX = min(srcEast, destEast)
            for (sideX in fromX until toX) {
                if (flags[sideX, destY, level] and CollisionFlag.WALL_SOUTH == 0) {
                    return true
                }
            }
        }
        return false
    }
}
