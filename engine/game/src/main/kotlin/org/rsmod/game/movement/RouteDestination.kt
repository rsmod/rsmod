package org.rsmod.game.movement

import java.util.ArrayDeque
import java.util.Deque
import org.rsmod.map.CoordGrid

public data class RouteDestination(
    private val waypoints: Deque<CoordGrid> = ArrayDeque<CoordGrid>()
) {
    public val size: Int
        get() = waypoints.size

    public fun add(coord: CoordGrid) {
        waypoints.add(coord)
    }

    public fun addAll(coords: Iterable<CoordGrid>) {
        waypoints.addAll(coords)
    }

    public fun clear() {
        waypoints.clear()
    }

    public fun peekFirst(): CoordGrid? = waypoints.peekFirst()

    public fun pollFirst(): CoordGrid? = waypoints.pollFirst()

    public fun isEmpty(): Boolean = waypoints.isEmpty()

    public fun isNotEmpty(): Boolean = waypoints.isNotEmpty()

    public fun toList(): List<CoordGrid> = waypoints.toList()
}
