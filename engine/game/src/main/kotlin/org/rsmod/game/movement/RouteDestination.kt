package org.rsmod.game.movement

import java.util.ArrayDeque
import java.util.Deque
import org.rsmod.map.CoordGrid

public data class RouteDestination(
    public val waypoints: Deque<CoordGrid> = ArrayDeque<CoordGrid>(),
    public var recalcRequest: RouteRequest? = null,
) : Deque<CoordGrid> by waypoints {
    internal fun abort() {
        waypoints.clear()
        recalcRequest = null
    }
}
