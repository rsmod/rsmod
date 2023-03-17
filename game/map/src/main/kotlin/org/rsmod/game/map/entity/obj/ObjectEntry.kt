package org.rsmod.game.map.entity.obj

import org.rsmod.game.map.Coordinates

public data class ObjectEntry(
    public val slot: Int,
    public val coords: Coordinates,
    public val entity: ObjectEntity
)
