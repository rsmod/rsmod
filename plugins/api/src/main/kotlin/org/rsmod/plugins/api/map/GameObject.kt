package org.rsmod.plugins.api.map

import org.rsmod.game.map.Coordinates
import org.rsmod.game.map.entity.obj.ObjectEntity
import org.rsmod.plugins.api.cache.type.obj.ObjectType

public class GameObject(
    public val type: ObjectType,
    public val coords: Coordinates,
    public val entity: ObjectEntity
) {

    public val id: Int get() = entity.id
    public val shape: Int get() = entity.shape
    public val rot: Int get() = entity.rot
}
