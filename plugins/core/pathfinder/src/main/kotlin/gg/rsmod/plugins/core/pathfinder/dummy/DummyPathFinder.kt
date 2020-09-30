package gg.rsmod.plugins.core.pathfinder.dummy

import gg.rsmod.game.model.domain.Direction
import gg.rsmod.game.model.domain.rayCast
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.plugins.core.pathfinder.PathFinder

class DummyPathFinder : PathFinder {

    override fun findPath(
        start: Coordinates,
        destination: Coordinates,
        destinationWidth: Int,
        destinationLength: Int
    ): List<Direction> {
        return start.rayCast(destination)
    }
}
