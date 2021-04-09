package org.rsmod.plugins.api.model.mob

import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.mob.Mob
import org.rsmod.plugins.api.model.angle
import org.rsmod.plugins.api.protocol.packet.update.DirectionMask
import org.rsmod.plugins.api.update.of

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
    orientate(dir.angle)
}

fun Mob.orientate(degrees: Int) {
    check(degrees < DEGREE_GRANULARITY) { "Angle out of bounds: $degrees (granularity=$DEGREE_GRANULARITY)" }
    val mask = DirectionMask.of(this, degrees)
    entity.updates.add(mask)
    orientation = degrees
}
