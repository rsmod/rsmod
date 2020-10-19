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

fun Coordinates.rayCast(destination: Coordinates): List<Direction> {
    var diffX = x - destination.x
    var diffY = y - destination.y

    val steps = abs(diffX).coerceAtLeast(abs(diffY))
    if (steps == 0) {
        return emptyList()
    }

    val directions = mutableListOf<Direction>()
    repeat(steps) {
        var north = false
        var east = false
        var south = false
        var west = false

        if (diffX < 0) {
            diffX++
            east = true
        } else if (diffX > 0) {
            diffX--
            west = true
        }

        if (diffY < 0) {
            diffY++
            north = true
        } else if (diffY > 0) {
            diffY--
            south = true
        }

        val direction: Direction = when {
            north -> when {
                east -> Direction.NorthEast
                west -> Direction.NorthWest
                else -> Direction.North
            }
            south -> when {
                east -> Direction.SouthEast
                west -> Direction.SouthWest
                else -> Direction.South
            }
            east -> Direction.East
            west -> Direction.West
            else -> return@repeat
        }

        directions.add(direction)
    }
    return directions
}
