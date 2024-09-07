package org.rsmod.game.movement

import java.util.Deque
import java.util.LinkedList
import org.rsmod.map.CoordGrid

public data class RouteDestination(
    public val waypoints: Deque<CoordGrid> = LinkedList(),
    public var recalcRequest: RouteRequest? = null,
) : Deque<CoordGrid> by waypoints {
    internal fun abort() {
        waypoints.clear()
        recalcRequest = null
    }
}
