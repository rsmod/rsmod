package org.rsmod.api.route

import jakarta.inject.Inject
import kotlin.math.sign
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntityAvatar
import org.rsmod.game.movement.MoveRestrict
import org.rsmod.game.movement.RouteRequest
import org.rsmod.game.movement.RouteRequestCoord
import org.rsmod.game.movement.RouteRequestLoc
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.RouteFinding
import org.rsmod.routefinder.StepValidator
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.collision.CollisionStrategy
import org.rsmod.routefinder.flag.CollisionFlag

public class StepFactory @Inject constructor(flags: CollisionFlagMap) {
    private val validator = StepValidator(flags)

    public fun createPath(
        source: CoordGrid,
        destination: CoordGrid,
        size: Int = 1,
        extraFlag: Int = 0,
        collision: CollisionStrategy,
    ): List<CoordGrid> {
        val coords = mutableListOf<CoordGrid>()
        var curr = CoordGrid(source.packed)
        for (i in 0 until 128 * 128) {
            if (curr == destination) break
            curr = validated(curr, destination, size, extraFlag, collision)
            if (curr == CoordGrid.NULL) break
            coords += curr
        }
        return coords
    }

    /**
     * @return the next available step in between [source] and [destination] _without_ validating
     *   that said step is not blocked by any possible collision flags.
     */
    public fun unvalidated(source: CoordGrid, destination: CoordGrid): CoordGrid {
        require(source != destination) { "`source` must not be equal to `destination`." }
        val offX = (destination.x - source.x).sign
        val offZ = (destination.z - source.z).sign
        return source.translate(offX, offZ)
    }

    /**
     * @return The next _validated_ step in between [source] and [dest]. [CoordGrid.NULL] if no tile
     *   could be validated between the two given coordinates.
     */
    public fun validated(
        source: CoordGrid,
        dest: CoordGrid,
        size: Int = 1,
        extraFlag: Int = 0,
        collision: CollisionStrategy = CollisionStrategy.Normal,
    ): CoordGrid {
        require(source != dest) { "`source` must not be equal to `dest`. ($source)" }
        val level = source.level
        val signX = (dest.x - source.x).sign
        val signZ = (dest.z - source.z).sign

        val diagonal =
            validator.canTravel(
                level = level,
                x = source.x,
                z = source.z,
                offsetX = signX,
                offsetZ = signZ,
                size = size,
                extraFlag = extraFlag,
                collision = collision,
            )
        if (diagonal) return source.translate(signX, signZ)

        val horizontal =
            signX != 0 &&
                validator.canTravel(
                    level = level,
                    x = source.x,
                    z = source.z,
                    offsetX = signX,
                    offsetZ = 0,
                    size = size,
                    extraFlag = extraFlag,
                    collision = collision,
                )
        if (horizontal) return source.translate(signX, 0)

        val vertical =
            signZ != 0 &&
                validator.canTravel(
                    level = level,
                    x = source.x,
                    z = source.z,
                    offsetX = 0,
                    offsetZ = signZ,
                    size = size,
                    extraFlag = extraFlag,
                    collision = collision,
                )
        if (vertical) return source.translate(0, signZ)

        return CoordGrid.NULL
    }

    public fun validated(
        npc: Npc,
        from: CoordGrid,
        dest: CoordGrid,
        collision: CollisionStrategy,
    ): CoordGrid =
        validated(
            source = from,
            dest = dest,
            size = npc.size,
            extraFlag = npc.validatedCollisionFlag(collision),
            collision = collision,
        )

    public fun instantDestination(request: RouteRequest, source: PathingEntityAvatar): CoordGrid =
        when (request) {
            is RouteRequestCoord -> request.destination
            is RouteRequestPathingEntity -> request.instantDestination(source)
            is RouteRequestLoc -> request.instantDestination(source)
        }

    private fun RouteRequestPathingEntity.instantDestination(
        source: PathingEntityAvatar
    ): CoordGrid {
        val dest =
            RouteFinding.naiveDestination(
                sourceX = source.x,
                sourceZ = source.z,
                sourceWidth = source.size,
                sourceLength = source.size,
                targetX = destination.x,
                targetZ = destination.z,
                targetWidth = destination.size,
                targetLength = destination.size,
            )
        return CoordGrid(dest.packed)
    }

    private fun RouteRequestLoc.instantDestination(source: PathingEntityAvatar): CoordGrid {
        val dest =
            RouteFinding.naiveDestination(
                sourceX = source.x,
                sourceZ = source.z,
                sourceWidth = source.size,
                sourceLength = source.size,
                targetX = destination.x,
                targetZ = destination.z,
                targetWidth = width,
                targetLength = length,
                targetAngle = angle,
            )
        return CoordGrid(dest.packed)
    }

    private fun Npc.validatedCollisionFlag(collision: CollisionStrategy): Int =
        // Npcs with "Blocked" MoveRestrict can walk under other npcs
        if (collision == CollisionStrategy.Blocked) {
            0
        } else if (moveRestrict == MoveRestrict.PassThru) {
            0
        } else {
            CollisionFlag.BLOCK_NPCS
        }
}
