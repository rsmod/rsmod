package org.rsmod.game.model.mob

import org.rsmod.game.model.client.Entity
import org.rsmod.game.model.map.Coordinates

public sealed class Mob {

    public abstract val entity: Entity

    public var index: Int
        get() = entity.index
        set(value) { entity.index = value }

    public var coords: Coordinates
        get() = entity.coords
        set(value) { entity.coords = value }

    public var prevCoords: Coordinates
        get() = entity.prevCoords
        set(value) { entity.prevCoords = value }
}
