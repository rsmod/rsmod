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
        x: Int,
        z: Int,
        level: Int,
        destX: Int,
        destZ: Int,
        destWidth: Int,
        destHeight: Int,
        srcSize: Int,
        rotation: Int,
        shape: Int,
        accessBitMask: Int
    ): Boolean {
        val exitStrategy = shape.exitStrategy
        if (exitStrategy != RECTANGLE_EXCLUSIVE_STRATEGY && x == destX && z == destZ) return true
        return when (exitStrategy) {
            WALL_STRATEGY -> reachWall(
                flags,
                x,
                z,
                level,
                destX,
                destZ,
                srcSize,
                shape,
                rotation
            )
            WALL_DECO_STRATEGY -> reachWallDeco(
                flags,
                x,
                z,
                level,
                destX,
                destZ,
                srcSize,
                shape,
                rotation
            )
            RECTANGLE_STRATEGY -> reachRectangle(
                flags,
                x,
                z,
                level,
                accessBitMask,
                destX,
                destZ,
                srcSize,
                destWidth,
                destHeight
            )
            RECTANGLE_EXCLUSIVE_STRATEGY -> reachExclusiveRectangle(
                flags,
                x,
                z,
                level,
                accessBitMask,
                destX,
                destZ,
                srcSize,
                destWidth,
                destHeight
            )
            else -> false
        }
    }

    private val Int.exitStrategy: Int
        get() = when {
            this == -2 -> RECTANGLE_EXCLUSIVE_STRATEGY
            this == -1 -> NO_STRATEGY
            this in 0..3 || this == 9 -> WALL_STRATEGY
            this < 9 -> WALL_DECO_STRATEGY
            this in 10..11 || this == 22 -> RECTANGLE_STRATEGY
            else -> NO_STRATEGY
        }
}
