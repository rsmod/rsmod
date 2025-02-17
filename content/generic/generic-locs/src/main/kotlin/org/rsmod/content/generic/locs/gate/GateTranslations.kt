package org.rsmod.content.generic.locs.gate

import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocShape
import org.rsmod.map.util.Translation

object GateTranslations {
    fun leftGateRightPair(shape: LocShape, angle: LocAngle): Translation =
        when {
            shape == LocShape.WallStraight && angle == LocAngle.West -> Translation(z = 1)
            shape == LocShape.WallStraight && angle == LocAngle.North -> Translation(x = 1)
            shape == LocShape.WallStraight && angle == LocAngle.East -> Translation(z = -1)
            shape == LocShape.WallStraight && angle == LocAngle.South -> Translation(x = -1)
            else -> throw NotImplementedError("Unhandled parameters: shape=$shape, angle=$angle")
        }

    fun leftGateOpen(shape: LocShape, angle: LocAngle): Translation =
        when {
            shape == LocShape.WallStraight && angle == LocAngle.West -> Translation(x = -1)
            shape == LocShape.WallStraight && angle == LocAngle.North -> Translation(z = 1)
            shape == LocShape.WallStraight && angle == LocAngle.East -> Translation(x = 1)
            shape == LocShape.WallStraight && angle == LocAngle.South -> Translation(z = -1)
            else -> throw NotImplementedError("Unhandled parameters: shape=$shape, angle=$angle")
        }

    fun rightGateOpen(shape: LocShape, angle: LocAngle): Translation =
        when {
            shape == LocShape.WallStraight && angle == LocAngle.West -> Translation(x = -2, z = -1)
            shape == LocShape.WallStraight && angle == LocAngle.North -> Translation(x = -1, z = 2)
            shape == LocShape.WallStraight && angle == LocAngle.East -> Translation(x = 2, z = 1)
            shape == LocShape.WallStraight && angle == LocAngle.South -> Translation(x = 1, z = -2)
            else -> throw NotImplementedError("Unhandled parameters: shape=$shape, angle=$angle")
        }

    fun leftGateClose(shape: LocShape, angle: LocAngle): Translation =
        leftGateOpen(shape, angle.turn(rotations = 3))

    fun rightGateClose(shape: LocShape, angle: LocAngle): Translation =
        rightGateOpen(shape, angle.turn(rotations = 3))
}
