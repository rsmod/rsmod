package gg.rsmod.plugins.content.pathfinder.dummy

import gg.rsmod.game.model.domain.rayCast
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.plugins.content.pathfinder.PathFinder

class DummyPathFinder : PathFinder {

    override fun findPath(
        start: Coordinates,
        destination: Coordinates,
        destinationWidth: Int,
        destinationLength: Int
    ): List<Coordinates> {
        return start.rayCast(destination)
    }
}
