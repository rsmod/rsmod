package org.rsmod.plugins.api.model.map

import org.rsmod.game.model.map.BuildArea
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.map.MapSquare
import org.rsmod.game.model.map.Viewport

fun Coordinates.inSquareRadius(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
    return x in x1..x2 && y in y1..y2
}

fun Coordinates.isWithinDistance(other: Coordinates, radius: Int): Boolean {
    val x1 = other.x - radius
    val y1 = other.y - radius
    val x2 = other.x + radius
    val y2 = other.y + radius
    return level == other.level && inSquareRadius(x1, y1, x2, y2)
}

fun Coordinates.toInternalString(): String {
    val square = mapSquare()
    val base = square.coords(level)
    val local = this - base
    return "${level}_${square.x}_${square.y}_${local.x}_${local.y}"
}

fun Viewport.Companion.of(center: Coordinates, maps: List<MapSquare>): Viewport {
    val viewRadius = BuildArea.SIZE / BuildArea.REBUILD_BOUNDARY
    val baseZone = center.zone().translate(-viewRadius, -viewRadius)
    val baseCoords = baseZone.coords()
    return Viewport(baseCoords, maps)
}
