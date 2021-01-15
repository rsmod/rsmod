package org.rsmod.game.path

import java.util.Queue
import org.rsmod.game.model.map.Coordinates

interface PathFinder {

    fun findPath(
        start: Coordinates,
        dest: Coordinates,
        size: Int,
        moveNear: Boolean = true,
        objectSlot: Int? = null,
        validDirs: Int? = null
    ): Queue<Coordinates>
}
