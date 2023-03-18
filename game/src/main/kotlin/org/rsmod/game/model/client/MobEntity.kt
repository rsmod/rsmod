package org.rsmod.game.model.client

import org.rsmod.game.map.Coordinates

public abstract class MobEntity(
    size: Int,
    public var index: Int = INVALID_INDEX
) : Entity(width = size, height = size) {

    public var prevCoords: Coordinates = Coordinates.ZERO

    public companion object {

        public const val INVALID_INDEX: Int = -1
    }
}
