package org.rsmod.api.route

import jakarta.inject.Inject
import org.rsmod.game.map.Direction
import org.rsmod.map.CoordGrid
import org.rsmod.pathfinder.LineValidator
import org.rsmod.pathfinder.StepValidator
import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.pathfinder.collision.CollisionStrategy

public class RayCastValidator @Inject constructor(flags: CollisionFlagMap) {
    private val lineValidator: LineValidator = LineValidator(flags)
    private val stepValidator: StepValidator = StepValidator(flags)

    public fun hasLineOfSight(
        source: CoordGrid,
        destination: CoordGrid,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destLength: Int = 0,
        extraFlag: Int = 0,
    ): Boolean {
        require(source.level == destination.level) {
            "`source` and `destination` must be on same level."
        }
        return lineValidator.hasLineOfSight(
            level = source.level,
            srcX = source.x,
            srcZ = source.z,
            destX = destination.x,
            destZ = destination.z,
            srcSize = srcSize,
            destWidth = destWidth,
            destLength = destLength,
            extraFlag = extraFlag,
        )
    }

    public fun hasLineOfWalk(
        source: CoordGrid,
        destination: CoordGrid,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destLength: Int = 0,
        extraFlag: Int = 0,
    ): Boolean {
        require(source.level == destination.level) {
            "`source` and `destination` must be on same level."
        }
        return lineValidator.hasLineOfWalk(
            level = source.level,
            srcX = source.x,
            srcZ = source.z,
            destX = destination.x,
            destZ = destination.z,
            srcSize = srcSize,
            destWidth = destWidth,
            destLength = destLength,
            extraFlag = extraFlag,
        )
    }

    public fun canStep(
        source: CoordGrid,
        direction: Direction,
        extraFlag: Int,
        srcSize: Int = 1,
        collision: CollisionStrategy = CollisionStrategy.Normal,
    ): Boolean {
        return stepValidator.canTravel(
            level = source.level,
            x = source.x,
            z = source.z,
            offsetX = direction.xOff,
            offsetZ = direction.zOff,
            size = srcSize,
            extraFlag = extraFlag,
            collision = collision,
        )
    }
}