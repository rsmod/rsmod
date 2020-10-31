package gg.rsmod.plugins.content.pathfinder

import gg.rsmod.game.model.map.Coordinates

interface PathFinder {

    fun findPath(
        start: Coordinates,
        destination: Coordinates,
        destinationWidth: Int,
        destinationLength: Int
    ): List<Coordinates>
}
