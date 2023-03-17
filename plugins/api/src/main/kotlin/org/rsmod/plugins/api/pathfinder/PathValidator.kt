package org.rsmod.plugins.api.pathfinder

import org.rsmod.game.map.Coordinates
import org.rsmod.game.pathfinder.LineValidator
import org.rsmod.game.pathfinder.StepValidator
import org.rsmod.plugins.api.map.GameMap
import org.rsmod.plugins.api.model.Direction
import javax.inject.Inject

public class PathValidator @Inject constructor(map: GameMap) {

    private val lineValidator: LineValidator = LineValidator(map.flags)

    private val stepValidator: StepValidator = StepValidator(map.flags)

    public fun hasLineOfSight(
        source: Coordinates,
        destination: Coordinates,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destHeight: Int = 0
    ): Boolean {
        require(source.level == destination.level) { "`source` and `destination` must be on same level." }
        return lineValidator.hasLineOfSight(
            level = source.level,
            srcX = source.x,
            srcZ = source.z,
            destX = destination.x,
            destZ = destination.z,
            srcSize = srcSize,
            destWidth = destWidth,
            destHeight = destHeight
        )
    }

    public fun hasLineOfWalk(
        source: Coordinates,
        destination: Coordinates,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destHeight: Int = 0
    ): Boolean {
        require(source.level == destination.level) { "`source` and `destination` must be on same level." }
        return lineValidator.hasLineOfWalk(
            level = source.level,
            srcX = source.x,
            srcZ = source.z,
            destX = destination.x,
            destZ = destination.z,
            srcSize = srcSize,
            destWidth = destWidth,
            destHeight = destHeight
        )
    }

    public fun canStep(
        source: Coordinates,
        direction: Direction,
        extraFlag: Int,
        srcSize: Int = 1,
        collision: CollisionType = CollisionType.Normal
    ): Boolean {
        return stepValidator.canTravel(
            level = source.level,
            x = source.x,
            z = source.z,
            offsetX = direction.offX,
            offsetZ = direction.offZ,
            size = srcSize,
            extraFlag = extraFlag,
            collision = collision.strategy
        )
    }
}
