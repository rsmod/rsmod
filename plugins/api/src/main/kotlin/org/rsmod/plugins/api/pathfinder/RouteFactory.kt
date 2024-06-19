package org.rsmod.plugins.api.pathfinder

import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.client.Entity
import org.rsmod.game.pathfinder.PathFinder
import org.rsmod.game.pathfinder.Route
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugins.api.map.GameObject
import jakarta.inject.Inject

public class RouteFactory @Inject constructor(flags: CollisionFlagMap) {

    private val pathFinder: PathFinder = PathFinder(flags)

    private val threadLocalPathFinder: ThreadLocal<PathFinder> = ThreadLocal.withInitial {
        PathFinder(flags)
    }

    private var asynchronous: Boolean = false

    public fun create(
        source: Coordinates,
        destination: Coordinates,
        collision: CollisionType = CollisionType.Normal,
        async: Boolean = asynchronous
    ): Route = with(pathFinder(async)) {
        findPath(
            level = source.level,
            srcX = source.x,
            srcZ = source.z,
            destX = destination.x,
            destZ = destination.z,
            srcSize = 1,
            destWidth = 1,
            destHeight = 1,
            collision = collision.strategy
        )
    }

    public fun create(
        source: Entity,
        destination: Coordinates,
        collision: CollisionType = CollisionType.Normal,
        async: Boolean = asynchronous
    ): Route = with(pathFinder(async)) {
        findPath(
            level = source.coords.level,
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = destination.x,
            destZ = destination.z,
            srcSize = source.size,
            destWidth = 1,
            destHeight = 1,
            collision = collision.strategy
        )
    }

    public fun create(
        source: Entity,
        destination: Entity,
        collision: CollisionType = CollisionType.Normal,
        async: Boolean = asynchronous
    ): Route = with(pathFinder(async)) {
        findPath(
            level = source.coords.level,
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = destination.coords.x,
            destZ = destination.coords.z,
            srcSize = source.size,
            destWidth = destination.width,
            destHeight = destination.height,
            collision = collision.strategy,
            objShape = RECTANGLE_EXCLUSIVE_STRATEGY
        )
    }

    public fun create(
        source: Entity,
        destination: GameObject,
        collision: CollisionType = CollisionType.Normal,
        async: Boolean = asynchronous
    ): Route = with(pathFinder(async)) {
        findPath(
            level = source.coords.level,
            srcX = source.coords.x,
            srcZ = source.coords.z,
            destX = destination.coords.x,
            destZ = destination.coords.z,
            srcSize = source.size,
            destWidth = destination.width,
            destHeight = destination.height,
            objShape = destination.shape,
            objRot = destination.rot,
            blockAccessFlags = destination.type.blockApproach,
            collision = collision.strategy
        )
    }

    private fun pathFinder(async: Boolean): PathFinder = if (async) {
        threadLocalPathFinder.get()
    } else {
        pathFinder
    }

    public companion object {

        private const val RECTANGLE_EXCLUSIVE_STRATEGY: Int = -2
    }
}
