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

    public val shape: Int
        get() = entity.shape

    public val angle: Int
        get() = entity.angle

    public fun shape(): LocShape = LocShape[shape]

    public fun angle(): LocAngle = LocAngle[angle]

    public fun turnAngle(rotations: Int = 1): LocAngle = angle().turn(rotations)

    public infix fun isAssociatedWith(type: LocType): Boolean = type.internalId == id
}
