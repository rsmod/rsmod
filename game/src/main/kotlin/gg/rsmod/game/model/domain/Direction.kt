package gg.rsmod.game.model.domain

import gg.rsmod.game.model.map.Coordinates
import kotlin.math.abs

private const val NEUTRAL_UNIT = 0
private const val POSITIVE_UNIT = 1
private const val NEGATIVE_UNIT = -1

sealed class Direction(val x: Int = NEUTRAL_UNIT, val y: Int = NEUTRAL_UNIT) {
    object North : Direction(y = POSITIVE_UNIT)
    object East : Direction(x = POSITIVE_UNIT)
    object South : Direction(y = NEGATIVE_UNIT)
    object West : Direction(x = NEGATIVE_UNIT)
    object NorthEast : Direction(x = POSITIVE_UNIT, y = POSITIVE_UNIT)
    object SouthEast : Direction(x = POSITIVE_UNIT, y = NEGATIVE_UNIT)
    object SouthWest : Direction(x = NEGATIVE_UNIT, y = NEGATIVE_UNIT)
    object NorthWest : Direction(x = NEGATIVE_UNIT, y = POSITIVE_UNIT)
}

fun Coordinates.translate(direction: Direction) = translate(direction.x, direction.y)

fun Coordinates.rayCast(destination: Coordinates): List<Coordinates> {
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
