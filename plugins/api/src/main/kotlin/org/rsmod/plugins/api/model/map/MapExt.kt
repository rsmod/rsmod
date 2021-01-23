package org.rsmod.plugins.api.model.map

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

fun Viewport.Companion.of(center: Coordinates, maps: List<MapSquare>): Viewport {
    val base = center.zone().translate(-6, -6).coords()
    return Viewport(base, maps)
}
