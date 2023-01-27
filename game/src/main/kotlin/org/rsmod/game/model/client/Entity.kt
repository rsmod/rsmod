package org.rsmod.game.model.client

import org.rsmod.game.model.map.Coordinates

public sealed class Entity(
    public var index: Int = -1,
    public var coords: Coordinates = Coordinates.ZERO
)
