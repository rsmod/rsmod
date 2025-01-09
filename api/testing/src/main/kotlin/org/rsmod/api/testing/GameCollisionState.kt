package org.rsmod.api.testing

import org.rsmod.api.route.BoundValidator
import org.rsmod.api.route.RayCastFactory
import org.rsmod.api.route.RayCastValidator
import org.rsmod.api.route.RouteFactory
import org.rsmod.api.route.StepFactory
import org.rsmod.game.map.Direction
import org.rsmod.game.map.collision.add
import org.rsmod.game.map.translate
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.flag.CollisionFlag

public data class GameCollisionState(
    public val collision: CollisionFlagMap,
    public val routeFactory: RouteFactory,
    public val rayCastFactory: RayCastFactory,
    public val stepFactory: StepFactory,
    public val rayCastValidator: RayCastValidator,
    public val boundValidator: BoundValidator,
) {
    public fun allocateCollision(coords: CoordGrid) {
        collision.allocateIfAbsent(coords.x, coords.z, coords.level)
    }

    public fun allocateCollision(coords: Collection<CoordGrid>) {
        coords.forEach(::allocateCollision)
    }

    public fun allocateCollision(zone: ZoneKey) {
        allocateCollision(zone.toCoords())
    }

    public fun allocateCollision(mapSquare: MapSquareKey) {
        for (level in 0 until CoordGrid.LEVEL_COUNT) {
            for (dz in 0 until MapSquareGrid.LENGTH step ZoneGrid.LENGTH) {
                for (dx in 0 until MapSquareGrid.LENGTH step ZoneGrid.LENGTH) {
                    val coords = mapSquare.toCoords(level).translate(dx, dz)
                    allocateCollision(coords)
                }
            }
        }
    }

    public fun blockDirections(centre: CoordGrid, directions: Iterable<Direction>) {
        directions.forEach { dir ->
            val offset = centre.translate(dir)
            collision.add(offset, CollisionFlag.LOC)
        }
    }
}
