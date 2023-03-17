package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap

internal fun reachRectangle(
    flags: CollisionFlagMap,
    x: Int,
    z: Int,
    level: Int,
    accessBitMask: Int,
    destX: Int,
    destZ: Int,
    srcSize: Int,
    destWidth: Int,
    destHeight: Int
): Boolean = when {
    srcSize > 1 -> {
        RectangleBoundaryUtils.collides(x, z, destX, destZ, srcSize, srcSize, destWidth, destHeight) ||
            RectangleBoundaryUtils.reachRectangleN(
                flags,
                x,
                z,
                level,
                accessBitMask,
                destX,
                destZ,
                srcSize,
                srcSize,
                destWidth,
                destHeight
            )
    }

    else ->
        RectangleBoundaryUtils.collides(x, z, destX, destZ, srcSize, srcSize, destWidth, destHeight) ||
            RectangleBoundaryUtils.reachRectangle1(
                flags,
                x,
                z,
                level,
                accessBitMask,
                destX,
                destZ,
                destWidth,
                destHeight
            )
}
