package org.rsmod.game.loc

import org.rsmod.game.type.loc.LocType
import org.rsmod.map.CoordGrid

public data class LocInfo(
    public val layer: Int,
    public val coords: CoordGrid,
    public val entity: LocEntity,
) {
    public val id: Int
        get() = entity.id

    public val shape: LocShape
        get() = LocShape[shapeId]

    public val shapeId: Int
        get() = entity.shape

    public val angle: LocAngle
        get() = LocAngle[angleId]

    public val angleId: Int
        get() = entity.angle

    public fun turnAngle(rotations: Int = 1): LocAngle = angle.turn(rotations)

    public fun isType(type: LocType): Boolean = type.internalId == id
}
