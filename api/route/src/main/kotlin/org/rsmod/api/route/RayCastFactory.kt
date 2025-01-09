package org.rsmod.api.route

import jakarta.inject.Inject
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.LineRouteFinding
import org.rsmod.routefinder.RayCast
import org.rsmod.routefinder.collision.CollisionFlagMap

public class RayCastFactory @Inject constructor(flags: CollisionFlagMap) {
    private val linePathFinder: LineRouteFinding = LineRouteFinding(flags)

    public fun createLineOfSight(
        source: CoordGrid,
        destination: CoordGrid,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destLength: Int = 0,
        extraFlag: Int = 0,
    ): RayCast {
        require(source.level == destination.level) {
            "`source` and `destination` must be on same level."
        }
        return linePathFinder.lineOfSight(
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

    public fun createLineOfWalk(
        source: CoordGrid,
        destination: CoordGrid,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destLength: Int = 0,
        extraFlag: Int = 0,
    ): RayCast {
        require(source.level == destination.level) {
            "`source` and `destination` must be on same level."
        }
        return linePathFinder.lineOfWalk(
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
}
