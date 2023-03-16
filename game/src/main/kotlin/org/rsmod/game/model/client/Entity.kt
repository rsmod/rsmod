package org.rsmod.game.model.client

import org.rsmod.game.map.Coordinates

public sealed class Entity(
    public var index: Int = INVALID_INDEX,
    public var coords: Coordinates = Coordinates.ZERO,
    public var prevCoords: Coordinates = Coordinates.ZERO
) {

    public companion object {

        public const val INVALID_INDEX: Int = -1
    }
}
