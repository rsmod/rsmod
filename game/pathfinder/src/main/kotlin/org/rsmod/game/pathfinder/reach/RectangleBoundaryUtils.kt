package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.BlockAccessFlag
import org.rsmod.game.pathfinder.flag.CollisionFlag
import kotlin.math.max
import kotlin.math.min

/**
 * @author Kris | 12/09/2021
 */
public object RectangleBoundaryUtils {

    public fun collides(
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcWidth: Int,
        srcHeight: Int,
        destWidth: Int,
        destHeight: Int
    ): Boolean = if (srcX >= destX + destWidth || srcX + srcWidth <= destX) {
        false
    } else {
        srcZ < destZ + destHeight && destZ < srcHeight + srcZ
    }

    internal fun reachRectangle1(
        flags: CollisionFlagMap,
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        destWidth: Int,
        destHeight: Int,
        blockAccessFlags: Int
    ): Boolean {
        val east = destX + destWidth - 1
        val north = destZ + destHeight - 1

        if (srcX == destX - 1 && srcZ >= destZ && srcZ <= north &&
            (flags[srcX, srcZ, level] and CollisionFlag.WALL_EAST) == 0 &&
            (blockAccessFlags and BlockAccessFlag.BLOCK_WEST) == 0
        ) {
            return true
        }

        if (srcX == east + 1 && srcZ >= destZ && srcZ <= north &&
            (flags[srcX, srcZ, level] and CollisionFlag.WALL_WEST) == 0 &&
            (blockAccessFlags and BlockAccessFlag.BLOCK_EAST) == 0
        ) {
            return true
        }

        if (srcZ + 1 == destZ && srcX >= destX && srcX <= east &&
            (flags[srcX, srcZ, level] and CollisionFlag.WALL_NORTH) == 0 &&
            (blockAccessFlags and BlockAccessFlag.BLOCK_SOUTH) == 0

        ) {
            return true
        }

        return srcZ == north + 1 && srcX >= destX && srcX <= east &&
            (flags[srcX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0 &&
            (blockAccessFlags and BlockAccessFlag.BLOCK_NORTH) == 0
    }

    internal fun reachRectangleN(
        flags: CollisionFlagMap,
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcWidth: Int,
        srcHeight: Int,
        destWidth: Int,
        destHeight: Int,
        blockAccessFlags: Int
    ): Boolean {
        val srcEast = srcX + srcWidth
        val srcNorth = srcHeight + srcZ
        val destEast = destWidth + destX
        val destNorth = destHeight + destZ
        if (destEast == srcX && (blockAccessFlags and BlockAccessFlag.BLOCK_EAST) == 0) {
            val fromZ = max(srcZ, destZ)
            val toZ = min(srcNorth, destNorth)
            for (sideZ in fromZ until toZ) {
                if (flags[destEast - 1, sideZ, level] and CollisionFlag.WALL_EAST == 0) {
                    return true
                }
            }
        } else if (srcEast == destX && (blockAccessFlags and BlockAccessFlag.BLOCK_WEST) == 0) {
            val fromZ = max(srcZ, destZ)
            val toZ = min(srcNorth, destNorth)
            for (sideZ in fromZ until toZ) {
                if (flags[destX, sideZ, level] and CollisionFlag.WALL_WEST == 0) {
                    return true
                }
            }
        } else if (srcZ == destNorth && (blockAccessFlags and BlockAccessFlag.BLOCK_NORTH) == 0) {
            val fromX = max(srcX, destX)
            val toX = min(srcEast, destEast)
            for (sideX in fromX until toX) {
                if (flags[sideX, destNorth - 1, level] and CollisionFlag.WALL_NORTH == 0) {
                    return true
                }
            }
        } else if (destZ == srcNorth && (blockAccessFlags and BlockAccessFlag.BLOCK_SOUTH) == 0) {
            val fromX = max(srcX, destX)
            val toX = min(srcEast, destEast)
            for (sideX in fromX until toX) {
                if (flags[sideX, destZ, level] and CollisionFlag.WALL_SOUTH == 0) {
                    return true
                }
            }
        }
        return false
    }
}
