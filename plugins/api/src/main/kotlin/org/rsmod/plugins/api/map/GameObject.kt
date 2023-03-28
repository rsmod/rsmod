package org.rsmod.plugins.api.map

import org.rsmod.game.map.Coordinates
import org.rsmod.game.map.entity.obj.ObjectEntity
import org.rsmod.plugins.cache.config.obj.ObjectType

public class GameObject(
    public val type: ObjectType,
    public val coords: Coordinates,
    public val entity: ObjectEntity
) {

    public val id: Int get() = entity.id

    public val shape: Int get() = entity.shape

    public val rot: Int get() = entity.rot

    public val width: Int get() = type.width

    public val height: Int get() = type.height

    public fun shape(): ObjectShape? {
        return ObjectShape.mapped[shape]
    }

    public fun slot(): ObjectSlot? {
        return shape()?.slot
    }

    public override fun toString(): String {
        return "GameObject(id=$id, shape=$shape, rot=$rot, coords=$coords)"
    }
}
