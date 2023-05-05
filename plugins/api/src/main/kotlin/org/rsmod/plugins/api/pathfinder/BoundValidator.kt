package org.rsmod.plugins.api.pathfinder

import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.client.Entity
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.reach.ReachStrategy
import org.rsmod.game.pathfinder.reach.RectangleBoundaryUtils
import org.rsmod.plugins.api.map.GameObject
import javax.inject.Inject

public class BoundValidator @Inject constructor(private val flags: CollisionFlagMap) {

    public fun touches(source: Entity, target: Entity): Boolean {
        assertLevels(source.coords, target.coords)
        return ReachStrategy.reachExclusiveRectangle(
            flags = flags,
            level = source.coords.level,
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = target.coords.x,
            destZ = target.coords.z,
            destWidth = target.width,
            destHeight = target.height,
            srcSize = source.size,
            blockAccessFlags = 0
        )
    }

    public fun touches(source: Entity, target: GameObject): Boolean {
        assertLevels(source.coords, target.coords)
        return ReachStrategy.reached(
            flags = flags,
            level = source.coords.level,
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = target.coords.x,
            destZ = target.coords.z,
            destWidth = target.width,
            destHeight = target.height,
            srcSize = source.size,
            objRot = target.rot,
            objShape = target.shape,
            blockAccessFlags = rotate(target.rot, target.type.blockApproach)
        )
    }

    public fun collides(source: Entity, target: Entity): Boolean {
        assertLevels(source.coords, target.coords)
        return RectangleBoundaryUtils.collides(
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = target.coords.x,
            destZ = target.coords.z,
            srcWidth = source.width,
            srcHeight = source.height,
            destWidth = target.width,
            destHeight = target.height
        )
    }

    public fun collides(source: Entity, target: GameObject): Boolean {
        assertLevels(source.coords, target.coords)
        return RectangleBoundaryUtils.collides(
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = target.coords.x,
            destZ = target.coords.z,
            srcWidth = source.width,
            srcHeight = source.height,
            destWidth = target.width,
            destHeight = target.height
        )
    }

    private companion object {

        private fun rotate(rot: Int, blockAccessFlags: Int): Int = when (rot) {
            0 -> blockAccessFlags
            else -> ((blockAccessFlags shl rot) and 0xF) or (blockAccessFlags shr (4 - rot))
        }

        @Suppress("NOTHING_TO_INLINE")
        private inline fun assertLevels(a: Coordinates, b: Coordinates) {
            require(a.level == b.level) { "`source` and `target` must be on same level." }
        }
    }
}
