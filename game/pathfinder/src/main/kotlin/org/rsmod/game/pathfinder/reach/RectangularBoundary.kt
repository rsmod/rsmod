package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap

internal fun reachRectangle(
    flags: CollisionFlagMap,
    x: Int,
    y: Int,
    level: Int,
    accessBitMask: Int,
    destX: Int,
    destY: Int,
    srcSize: Int,
    destWidth: Int,
    destHeight: Int
): Boolean = when {
    srcSize > 1 -> {
        RectangleBoundaryUtils.collides(x, y, destX, destY, srcSize, srcSize, destWidth, destHeight) ||
            RectangleBoundaryUtils.reachRectangleN(
                flags,
                x,
                y,
                level,
                accessBitMask,
                destX,
                destY,
                srcSize,
                srcSize,
                destWidth,
                destHeight
            )
    }

    else ->
        RectangleBoundaryUtils.collides(x, y, destX, destY, srcSize, srcSize, destWidth, destHeight) ||
            RectangleBoundaryUtils.reachRectangle1(
                flags,
                x,
                y,
                level,
                accessBitMask,
                destX,
                destY,
                destWidth,
                destHeight
            )
}
