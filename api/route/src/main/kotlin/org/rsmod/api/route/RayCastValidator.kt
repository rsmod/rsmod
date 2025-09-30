package org.rsmod.api.route

import jakarta.inject.Inject
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.LineValidator
import org.rsmod.routefinder.collision.CollisionFlagMap

public class RayCastValidator @Inject constructor(flags: CollisionFlagMap) {
    private val lineValidator: LineValidator = LineValidator(flags)

    public fun hasLineOfSight(
        source: CoordGrid,
        destination: CoordGrid,
        srcWidth: Int = 1,
        srcLength: Int = 1,
        destWidth: Int = 1,
        destLength: Int = 1,
        extraFlag: Int = 0,
    ): Boolean {
        require(source.level == destination.level) {
            "`source` and `destination` must be on same level. (source=$source, dest=$destination)"
        }
        require(destWidth > 0 && destLength > 0) {
            "Dest width and length should always be > 0. (dimensions=${destWidth}x${destLength})"
        }
        return lineValidator.hasLineOfSight(
            level = source.level,
            srcX = source.x,
            srcZ = source.z,
            destX = destination.x,
            destZ = destination.z,
            srcWidth = srcWidth,
            srcLength = srcLength,
            destWidth = destWidth,
            destLength = destLength,
            extraFlag = extraFlag,
        )
    }

    public fun hasLineOfWalk(
        source: CoordGrid,
        destination: CoordGrid,
        srcWidth: Int = 1,
        srcLength: Int = 1,
        destWidth: Int = 1,
        destLength: Int = 1,
        extraFlag: Int = 0,
    ): Boolean {
        require(source.level == destination.level) {
            "`source` and `destination` must be on same level. (source=$source, dest=$destination)"
        }
        require(destWidth > 0 && destLength > 0) {
            "Dest width and length should always be > 0. (dimensions=${destWidth}x${destLength})"
        }
        return lineValidator.hasLineOfWalk(
            level = source.level,
            srcX = source.x,
            srcZ = source.z,
            destX = destination.x,
            destZ = destination.z,
            srcWidth = srcWidth,
            srcLength = srcLength,
            destWidth = destWidth,
            destLength = destLength,
            extraFlag = extraFlag,
        )
    }
}
