package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap

internal fun reachRectangle(
    flags: CollisionFlagMap,
    level: Int,
    srcX: Int,
    srcZ: Int,
    destX: Int,
    destZ: Int,
    srcSize: Int,
    destWidth: Int,
    destHeight: Int,
    blockAccessFlags: Int
): Boolean = when {
    srcSize > 1 -> {
        RectangleBoundaryUtils.collides(srcX, srcZ, destX, destZ, srcSize, srcSize, destWidth, destHeight) ||
            RectangleBoundaryUtils.reachRectangleN(
                flags = flags,
                level = level,
                srcX = srcX,
                srcZ = srcZ,
                destX = destX,
                destZ = destZ,
                srcWidth = srcSize,
                srcHeight = srcSize,
                destWidth = destWidth,
                destHeight = destHeight,
                blockAccessFlags = blockAccessFlags
            )
    }
    else ->
        RectangleBoundaryUtils.collides(srcX, srcZ, destX, destZ, srcSize, srcSize, destWidth, destHeight) ||
            RectangleBoundaryUtils.reachRectangle1(
                flags = flags,
                level = level,
                srcX = srcX,
                srcZ = srcZ,
                destX = destX,
                destZ = destZ,
                destWidth = destWidth,
                destHeight = destHeight,
                blockAccessFlags = blockAccessFlags
            )
}
