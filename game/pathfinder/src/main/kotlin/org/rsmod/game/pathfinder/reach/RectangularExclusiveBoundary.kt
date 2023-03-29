package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap

/**
 * @author Kris | 12/09/2021
 */
internal fun reachExclusiveRectangle(
    flags: CollisionFlagMap,
    x: Int,
    z: Int,
    level: Int,
    blockAccessFlags: Int,
    destX: Int,
    destZ: Int,
    srcSize: Int,
    destWidth: Int,
    destHeight: Int
): Boolean = when {
    srcSize > 1 -> {
        if (RectangleBoundaryUtils.collides(x, z, destX, destZ, srcSize, srcSize, destWidth, destHeight)) {
            false
        } else {
            RectangleBoundaryUtils.reachRectangleN(
                flags,
                x,
                z,
                level,
                blockAccessFlags,
                destX,
                destZ,
                srcSize,
                srcSize,
                destWidth,
                destHeight
            )
        }
    }
    else -> {
        if (RectangleBoundaryUtils.collides(x, z, destX, destZ, srcSize, srcSize, destWidth, destHeight)) {
            false
        } else {
            RectangleBoundaryUtils.reachRectangle1(
                flags,
                x,
                z,
                level,
                blockAccessFlags,
                destX,
                destZ,
                destWidth,
                destHeight
            )
        }
    }
}
