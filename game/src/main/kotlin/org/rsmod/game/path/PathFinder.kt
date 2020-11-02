package org.rsmod.game.path

import org.rsmod.game.model.map.Coordinates

interface PathFinder {

    fun findPath(
        start: Coordinates,
        destination: Coordinates
    ): List<Coordinates>
}
