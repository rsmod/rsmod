package gg.rsmod.plugins.api

import gg.rsmod.game.model.map.MapSquare

fun MapSquare.viewport(): List<MapSquare> {
    val bottomLeft = translate(-1, -1)
    val topLeft = translate(-1, 0)
    val bottomRight = translate(0, -1)
    val topRight = this
    return listOf(bottomLeft, topLeft, bottomRight, topRight)
}
