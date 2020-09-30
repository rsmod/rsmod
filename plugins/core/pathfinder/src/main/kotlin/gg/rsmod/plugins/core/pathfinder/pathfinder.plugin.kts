package gg.rsmod.plugins.core.pathfinder

import gg.rsmod.plugins.core.protocol.packet.MapMove

val pathfinder: PathFinder by inject()

onAction<MapMove> {
    val directions = pathfinder.findPath(
        start = player.coords,
        destination = destination,
        destinationWidth = 0,
        destinationLength = 0
    )
    player.steps.clear()
    player.steps.addAll(directions)
}
