package gg.rsmod.plugins.content.pathfinder

import gg.rsmod.plugins.api.protocol.packet.MapMove

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
