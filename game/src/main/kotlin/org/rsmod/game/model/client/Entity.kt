package org.rsmod.game.model.client

import org.rsmod.game.map.Coordinates

public sealed class Entity(
    public val width: Int,
    public val height: Int,
    public var coords: Coordinates = Coordinates.ZERO
) {

    public val size: Int get() = width.coerceAtLeast(height)
}
