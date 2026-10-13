package org.runerealm.api.testing

import org.runerealm.api.route.BoundValidator
import org.runerealm.api.route.RayCastFactory
import org.runerealm.api.route.RayCastValidator
import org.runerealm.api.route.RouteFactory
import org.runerealm.api.route.StepFactory
import org.runerealm.game.map.Direction
import org.runerealm.game.map.collision.add
import org.runerealm.game.map.translate
import org.runerealm.map.CoordGrid
import org.runerealm.map.square.MapSquareGrid
import org.runerealm.map.square.MapSquareKey
import org.runerealm.map.zone.ZoneGrid
import org.runerealm.map.zone.ZoneKey
import org.runerealm.routefinder.collision.CollisionFlagMap
import org.runerealm.routefinder.flag.CollisionFlag

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
