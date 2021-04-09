package org.rsmod.plugins.api.model.mob

import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.domain.translate
import org.rsmod.game.model.mob.Mob
import org.rsmod.plugins.api.protocol.packet.update.DirectionMask
import org.rsmod.plugins.api.update.of
import kotlin.math.atan2

private const val DEGREE_GRANULARITY = 2048

private val ANGLED_DIRECTIONS = arrayOf(
    Direction.South,
    Direction.SouthWest,
    Direction.East,
    Direction.NorthWest,
    Direction.North,
    Direction.NorthEast,
    Direction.West,
    Direction.SouthEast
)

fun Mob.faceDirection(): Direction {
    val halfDirectionGranularity = DEGREE_GRANULARITY shr 4
    val orientation = orientation + halfDirectionGranularity
    val index = (orientation shr 8) and 0x7
    return ANGLED_DIRECTIONS[index]
}

fun Mob.faceDirection(dir: Direction) {
    val translation = coords.translate(dir)
    val atan = atan2(coords.x.toFloat() - translation.x, coords.y.toFloat() - translation.y)
    val degrees = (atan * 325.949).toInt() and 0x7FF
    orientate(degrees)
}

fun Mob.orientate(degrees: Int) {
    check(degrees < DEGREE_GRANULARITY) { "Angle out of bounds: $degrees (granularity=$DEGREE_GRANULARITY)" }
    val mask = DirectionMask.of(this, degrees)
    entity.updates.add(mask)
    orientation = degrees
}
