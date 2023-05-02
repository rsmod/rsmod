package org.rsmod.game.pathfinder.reach

import org.rsmod.game.pathfinder.collision.CollisionFlagMap

public object ReachStrategy {

    private const val WALL_STRATEGY = 0
    private const val WALL_DECO_STRATEGY = 1
    private const val RECTANGLE_STRATEGY = 2
    private const val NO_STRATEGY = 3
    private const val RECTANGLE_EXCLUSIVE_STRATEGY = 4

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
        objRot: Int,
        objShape: Int,
        blockAccessFlags: Int
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
                blockAccessFlags = blockAccessFlags
            )
            else -> false
        }
    }

    private fun exitStrategy(objShape: Int): Int = when {
        objShape == -2 -> RECTANGLE_EXCLUSIVE_STRATEGY
        objShape == -1 -> NO_STRATEGY
        objShape in 0..3 || objShape == 9 -> WALL_STRATEGY
        objShape < 9 -> WALL_DECO_STRATEGY
        objShape in 10..11 || objShape == 22 -> RECTANGLE_STRATEGY
        else -> NO_STRATEGY
    }
}
