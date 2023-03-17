package org.rsmod.plugins.api.movement

import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.mob.move.MovementQueue
import org.rsmod.game.pathfinder.Route

public fun MovementQueue.addAll(route: Route) {
    waypoints += route.map { Coordinates(it.x, it.z, it.level) }
}
