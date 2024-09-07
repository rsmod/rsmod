package org.rsmod.content.other.generic.doors

import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocShape
import org.rsmod.map.CoordGrid

object DoorTranslations {
    fun translateOpen(coords: CoordGrid, shape: LocShape, angle: LocAngle): CoordGrid =
        when {
            shape == LocShape.WallStraight && angle == LocAngle.West -> coords.translateX(-1)
            shape == LocShape.WallStraight && angle == LocAngle.North -> coords.translateZ(1)
            shape == LocShape.WallStraight && angle == LocAngle.East -> coords.translateX(1)
            shape == LocShape.WallStraight && angle == LocAngle.South -> coords.translateZ(-1)

            shape == LocShape.WallDiagonal && angle == LocAngle.West -> coords.translateZ(1)
            shape == LocShape.WallDiagonal && angle == LocAngle.North -> coords.translateX(1)
            shape == LocShape.WallDiagonal && angle == LocAngle.East -> coords.translateZ(-1)
            shape == LocShape.WallDiagonal && angle == LocAngle.South -> coords.translateX(-1)

            else -> coords
        }

    fun translateOpenOpposite(coords: CoordGrid, shape: LocShape, angle: LocAngle): CoordGrid =
        when {
            shape == LocShape.WallStraight && angle == LocAngle.West -> coords.translateX(1)
            shape == LocShape.WallStraight && angle == LocAngle.North -> coords.translateZ(-1)
            shape == LocShape.WallStraight && angle == LocAngle.East -> coords.translateX(-1)
            shape == LocShape.WallStraight && angle == LocAngle.South -> coords.translateZ(1)

            shape == LocShape.WallDiagonal && angle == LocAngle.West -> coords.translateZ(-1)
            shape == LocShape.WallDiagonal && angle == LocAngle.North -> coords.translateX(-1)
            shape == LocShape.WallDiagonal && angle == LocAngle.East -> coords.translateZ(1)
            shape == LocShape.WallDiagonal && angle == LocAngle.South -> coords.translateX(1)

            else -> coords
        }

    fun translateClose(coords: CoordGrid, shape: LocShape, angle: LocAngle): CoordGrid =
        when {
            shape == LocShape.WallStraight && angle == LocAngle.West -> coords.translateZ(1)
            shape == LocShape.WallStraight && angle == LocAngle.North -> coords.translateX(1)
            shape == LocShape.WallStraight && angle == LocAngle.East -> coords.translateZ(-1)
            shape == LocShape.WallStraight && angle == LocAngle.South -> coords.translateX(-1)

            shape == LocShape.WallDiagonal && angle == LocAngle.West -> coords.translateX(1)
            shape == LocShape.WallDiagonal && angle == LocAngle.North -> coords.translateZ(-1)
            shape == LocShape.WallDiagonal && angle == LocAngle.East -> coords.translateX(-1)
            shape == LocShape.WallDiagonal && angle == LocAngle.South -> coords.translateZ(1)

            else -> coords
        }

    fun translateCloseOpposite(coords: CoordGrid, shape: LocShape, angle: LocAngle): CoordGrid =
        when {
            shape == LocShape.WallStraight && angle == LocAngle.West -> coords.translateZ(-1)
            shape == LocShape.WallStraight && angle == LocAngle.North -> coords.translateX(-1)
            shape == LocShape.WallStraight && angle == LocAngle.East -> coords.translateZ(1)
            shape == LocShape.WallStraight && angle == LocAngle.South -> coords.translateX(1)

            shape == LocShape.WallDiagonal && angle == LocAngle.West -> coords.translateX(-1)
            shape == LocShape.WallDiagonal && angle == LocAngle.North -> coords.translateZ(1)
            shape == LocShape.WallDiagonal && angle == LocAngle.East -> coords.translateX(1)
            shape == LocShape.WallDiagonal && angle == LocAngle.South -> coords.translateZ(-1)

            else -> coords
        }
}
