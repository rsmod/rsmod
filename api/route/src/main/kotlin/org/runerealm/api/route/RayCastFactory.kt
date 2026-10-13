package org.runerealm.api.route

import jakarta.inject.Inject
import org.runerealm.map.CoordGrid
import org.runerealm.routefinder.LineRouteFinding
import org.runerealm.routefinder.RayCast
import org.runerealm.routefinder.collision.CollisionFlagMap

public class RayCastFactory @Inject constructor(flags: CollisionFlagMap) {
    private val linePathFinder: LineRouteFinding = LineRouteFinding(flags)

    public fun createLineOfSight(
        source: CoordGrid,
        destination: CoordGrid,
        srcWidth: Int = 1,
        srcLength: Int = 1,
        destWidth: Int = 1,
        destLength: Int = 1,
        extraFlag: Int = 0,
    ): RayCast {
        require(source.level == destination.level) {
            "`source` and `destination` must be on same level. (source=$source, dest=$destination)"
        }
        require(destWidth > 0 && destLength > 0) {
            "Dest width and length should always be > 0. (dimensions=${destWidth}x${destLength})"
        }
        return linePathFinder.lineOfSight(
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

    public fun createLineOfWalk(
        source: CoordGrid,
        destination: CoordGrid,
        srcWidth: Int = 1,
        srcLength: Int = 1,
        destWidth: Int = 1,
        destLength: Int = 1,
        extraFlag: Int = 0,
    ): RayCast {
        require(source.level == destination.level) {
            "`source` and `destination` must be on same level. (source=$source, dest=$destination)"
        }
        require(destWidth > 0 && destLength > 0) {
            "Dest width and length should always be > 0. (dimensions=${destWidth}x${destLength})"
        }
        return linePathFinder.lineOfWalk(
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
