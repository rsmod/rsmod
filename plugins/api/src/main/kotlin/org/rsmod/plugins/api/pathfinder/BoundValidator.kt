package org.rsmod.plugins.api.pathfinder

import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.client.Entity
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.reach.ReachStrategy
import org.rsmod.game.pathfinder.reach.RectangleBoundaryUtils
import org.rsmod.plugins.api.map.GameObject
import jakarta.inject.Inject

@Suppress("DuplicatedCode")
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
            srcSize = source.size
        )
    }

    public fun touches(source: Entity, target: GameObject): Boolean {
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
            srcSize = source.size
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

    /**
     * Similar to `touches`, but takes the [target]'s block approach bitflags
     * into account. This means that if the [target] has its east side blocked,
     * this method will only return true if [source] is touching it from any of
     * the other sides.
     */
    public fun touchesStrict(source: Entity, target: GameObject): Boolean {
        assertLevels(source.coords, target.coords)
        return ReachStrategy.reachRectangle(
            flags = flags,
            level = source.coords.level,
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = target.coords.x,
            destZ = target.coords.z,
            destWidth = target.width,
            destHeight = target.height,
            srcSize = source.size,
            blockAccessFlags = target.type.blockApproach
        )
    }

    private companion object {

        @Suppress("NOTHING_TO_INLINE")
        private inline fun assertLevels(a: Coordinates, b: Coordinates) {
            require(a.level == b.level) { "`source` and `target` must be on same level." }
        }
    }
}
