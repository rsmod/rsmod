package org.rsmod.plugins.content.pathfinder.dummy

import java.util.LinkedList
import java.util.Queue
import kotlin.math.abs
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.path.PathFinder

class DummyPathFinder : PathFinder {

    override fun findPath(
        start: Coordinates,
        dest: Coordinates,
        size: Int,
        moveNear: Boolean,
        objectShape: Int?,
        validDirs: Int?
    ): Queue<Coordinates> {
        return start.rayCast(dest)
    }
}

private fun Coordinates.rayCast(destination: Coordinates): Queue<Coordinates> {
    var diffX = x - destination.x
    var diffY = y - destination.y

    val steps = abs(diffX).coerceAtLeast(abs(diffY))
    if (steps == 0) {
        return LinkedList()
    }

    val coordinates = LinkedList<Coordinates>()
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
