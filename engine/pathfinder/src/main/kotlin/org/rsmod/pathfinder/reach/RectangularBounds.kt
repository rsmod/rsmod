package org.rsmod.pathfinder.reach

import kotlin.math.max
import kotlin.math.min
import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.pathfinder.flag.BlockAccessFlag
import org.rsmod.pathfinder.flag.CollisionFlag

/** @author Kris | 12/09/2021 */
public object RectangularBounds {
    public fun collides(
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcWidth: Int,
        srcLength: Int,
        destWidth: Int,
        destLength: Int,
    ): Boolean =
        if (srcX >= destX + destWidth || srcX + srcWidth <= destX) {
            false
        } else {
            srcZ < destZ + destLength && destZ < srcLength + srcZ
        }

    internal fun reachRectangle1(
        flags: CollisionFlagMap,
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        destWidth: Int,
        destLength: Int,
        blockAccessFlags: Int,
    ): Boolean {
        val east = destX + destWidth - 1
        val north = destZ + destLength - 1

        if (
            srcX == destX - 1 &&
                srcZ >= destZ &&
                srcZ <= north &&
                (flags[srcX, srcZ, level] and CollisionFlag.WALL_EAST) == 0 &&
                (blockAccessFlags and BlockAccessFlag.WEST) == 0
        ) {
            return true
        }

        if (
            srcX == east + 1 &&
                srcZ >= destZ &&
                srcZ <= north &&
                (flags[srcX, srcZ, level] and CollisionFlag.WALL_WEST) == 0 &&
                (blockAccessFlags and BlockAccessFlag.EAST) == 0
        ) {
            return true
        }

        if (
            srcZ + 1 == destZ &&
                srcX >= destX &&
                srcX <= east &&
                (flags[srcX, srcZ, level] and CollisionFlag.WALL_NORTH) == 0 &&
                (blockAccessFlags and BlockAccessFlag.SOUTH) == 0
        ) {

            return true
        }

        return srcZ == north + 1 &&
            srcX >= destX &&
            srcX <= east &&
            (flags[srcX, srcZ, level] and CollisionFlag.WALL_SOUTH) == 0 &&
            (blockAccessFlags and BlockAccessFlag.NORTH) == 0
    }

    internal fun reachRectangleN(
        flags: CollisionFlagMap,
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        srcWidth: Int,
        srcLength: Int,
        destWidth: Int,
        destLength: Int,
        blockAccessFlags: Int,
    ): Boolean {
        val srcEast = srcX + srcWidth
        val srcNorth = srcLength + srcZ
        val destEast = destWidth + destX
        val destNorth = destLength + destZ
        if (destEast == srcX && (blockAccessFlags and BlockAccessFlag.EAST) == 0) {
            val fromZ = max(srcZ, destZ)
            val toZ = min(srcNorth, destNorth)
            for (sideZ in fromZ until toZ) {
                if (flags[destEast - 1, sideZ, level] and CollisionFlag.WALL_EAST == 0) {
                    return true
                }
            }
        } else if (srcEast == destX && (blockAccessFlags and BlockAccessFlag.WEST) == 0) {
            val fromZ = max(srcZ, destZ)
            val toZ = min(srcNorth, destNorth)
            for (sideZ in fromZ until toZ) {
                if (flags[destX, sideZ, level] and CollisionFlag.WALL_WEST == 0) {
                    return true
                }
            }
        } else if (srcZ == destNorth && (blockAccessFlags and BlockAccessFlag.NORTH) == 0) {
            val fromX = max(srcX, destX)
            val toX = min(srcEast, destEast)
            for (sideX in fromX until toX) {
                if (flags[sideX, destNorth - 1, level] and CollisionFlag.WALL_NORTH == 0) {
                    return true
                }
            }
        } else if (destZ == srcNorth && (blockAccessFlags and BlockAccessFlag.SOUTH) == 0) {
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
