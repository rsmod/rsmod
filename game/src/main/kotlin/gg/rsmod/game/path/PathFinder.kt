package gg.rsmod.game.path

import gg.rsmod.game.model.map.Coordinates

interface PathFinder {

    fun findPath(
        start: Coordinates,
        destination: Coordinates,
        destinationWidth: Int,
        destinationLength: Int
    ): List<Coordinates>
}
