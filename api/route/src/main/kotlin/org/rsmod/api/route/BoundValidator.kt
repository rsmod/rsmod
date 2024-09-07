package org.rsmod.api.route

import jakarta.inject.Inject
import org.rsmod.game.entity.PathingEntityAvatar
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.map.CoordGrid
import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.pathfinder.loc.LocShapeConstants
import org.rsmod.pathfinder.reach.ReachStrategy
import org.rsmod.pathfinder.reach.RectangularBounds

public class BoundValidator @Inject constructor(private val flags: CollisionFlagMap) {
    public fun touches(source: PathingEntityAvatar, target: PathingEntityAvatar): Boolean {
        assertLevels(source.coords, target.coords)
        return ReachStrategy.reachExclusiveRectangle(
            flags = flags,
            level = source.coords.level,
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = target.coords.x,
            destZ = target.coords.z,
            destWidth = target.size,
            destLength = target.size,
            srcSize = source.size,
        )
    }

    public fun touches(source: PathingEntityAvatar, target: BoundLocInfo): Boolean {
        assertLevels(source.coords, target.coords)
        return ReachStrategy.reached(
            flags = flags,
            level = source.coords.level,
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = target.coords.x,
            destZ = target.coords.z,
            destWidth = target.width,
            destLength = target.length,
            srcSize = source.size,
            locAngle = target.angle,
            locShape = target.shape,
            blockAccessFlags = target.forceApproachFlags,
        )
    }

    public fun collides(source: PathingEntityAvatar, target: PathingEntityAvatar): Boolean {
        assertLevels(source.coords, target.coords)
        return RectangularBounds.collides(
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = target.coords.x,
            destZ = target.coords.z,
            srcWidth = source.size,
            srcLength = source.size,
            destWidth = target.size,
            destLength = target.size,
        )
    }

    public fun collides(source: PathingEntityAvatar, target: BoundLocInfo): Boolean {
        assertLevels(source.coords, target.coords)
        return RectangularBounds.collides(
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = target.coords.x,
            destZ = target.coords.z,
            srcWidth = source.size,
            srcLength = source.size,
            destWidth = target.adjustedWidth,
            destLength = target.adjustedLength,
        )
    }

    public fun collides(
        source: PathingEntityAvatar,
        target: LocInfo,
        type: UnpackedLocType,
    ): Boolean = collides(source, BoundLocInfo(target, type))

    public fun touches(
        source: PathingEntityAvatar,
        target: LocInfo,
        type: UnpackedLocType,
    ): Boolean = touches(source, BoundLocInfo(target, type))

    public fun touches(source: PathingEntityAvatar, target: Obj): Boolean {
        assertLevels(source.coords, target.coords)
        return ReachStrategy.reached(
            flags = flags,
            level = source.coords.level,
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = target.coords.x,
            destZ = target.coords.z,
            destWidth = 1,
            destLength = 1,
            srcSize = source.size,
            locShape = LocShapeConstants.CENTREPIECE_STRAIGHT,
        )
    }

    private companion object {
        @Suppress("NOTHING_TO_INLINE")
        private inline fun assertLevels(source: CoordGrid, target: CoordGrid) {
            require(source.level == target.level) { "`source` and `target` must be on same level." }
        }
    }
}
