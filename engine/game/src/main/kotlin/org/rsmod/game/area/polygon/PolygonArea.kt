package org.rsmod.game.area.polygon

import org.rsmod.map.square.MapSquareKey

public data class PolygonArea(val mapSquares: Map<MapSquareKey, PolygonMapSquare>) {
    public constructor(key: MapSquareKey, single: PolygonMapSquare) : this(mapOf(key to single))
}
