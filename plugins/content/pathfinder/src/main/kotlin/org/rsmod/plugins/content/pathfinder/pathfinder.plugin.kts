package org.rsmod.plugins.content.pathfinder

import org.rsmod.game.coroutine.delay
import org.rsmod.game.path.PathFinder
import org.rsmod.plugins.api.protocol.packet.MapMove
import org.rsmod.plugins.api.protocol.packet.ObjectClick

val pathfinder: PathFinder by inject()

onAction<MapMove> {
    val coordinates = pathfinder.findPath(
        start = player.coords,
        dest = destination,
        size = 1
    )
    player.steps.clear()
    player.steps.addAll(coordinates)
}

onAction<ObjectClick> {
    val coordinates = pathfinder.findPath(
        start = player.coords,
        dest = obj.coords,
        size = 1
    )
    player.steps.clear()
    player.steps.addAll(coordinates)
    player.normalQueue {
        if (coordinates.isNotEmpty()) {
            if (approach) {
                // TODO: delay with los check
            } else {
                delay { player.coords == coordinates.last() }
            }
            player.steps.clear()
        }
        actions.publish(action, obj.id)
    }
}
