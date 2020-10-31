package gg.rsmod.plugins.content.pathfinder.dummy

import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.path.PathFinder
import kotlin.math.abs

class DummyPathFinder : PathFinder {

    override fun findPath(
        start: Coordinates,
        destination: Coordinates,
        destinationWidth: Int,
        destinationLength: Int
    ): List<Coordinates> {
        return start.rayCast(destination)
    }
}

private fun Coordinates.rayCast(destination: Coordinates): List<Coordinates> {
    var diffX = x - destination.x
    var diffY = y - destination.y

    val steps = abs(diffX).coerceAtLeast(abs(diffY))
    if (steps == 0) {
        return emptyList()
    }

    val coordinates = mutableListOf<Coordinates>()
    var prev: Coordinates = this
    repeat(steps) {
        var translateX = 0
        var translateY = 0

        if (diffX < 0) {
            diffX++
            translateX = 1
        } else if (diffX > 0) {
            diffX--
            translateX = -1
        }

        if (diffY < 0) {
            diffY++
            translateY = 1
        } else if (diffY > 0) {
            diffY--
            translateY = -1
        }

        val next = prev.translate(translateX, translateY)
        coordinates.add(next)
        prev = next
    }
    return coordinates
}
