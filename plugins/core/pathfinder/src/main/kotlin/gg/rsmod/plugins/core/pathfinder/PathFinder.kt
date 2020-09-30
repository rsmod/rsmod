package gg.rsmod.plugins.core.pathfinder

import gg.rsmod.game.model.domain.Direction
import gg.rsmod.game.model.map.Coordinates

interface PathFinder {

    fun findPath(
        start: Coordinates,
        destination: Coordinates,
        destinationWidth: Int,
        destinationLength: Int
    ): List<Direction>
}
