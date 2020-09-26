package gg.rsmod.plugins.api.map

import gg.rsmod.game.model.map.MapIsolation
import gg.rsmod.game.model.map.MapSquare
import gg.rsmod.game.model.map.Zone

fun Zone.viewport(isolation: MapIsolation): List<MapSquare> {
    val lx = (x - 6) / Zone.SIZE
    val ly = (y - 6) / Zone.SIZE
    val rx = (x + 6) / Zone.SIZE
    val ry = (y + 6) / Zone.SIZE

    val viewport = mutableListOf<MapSquare>()
    for (mx in lx..rx) {
        for (my in ly..ry) {
            val mapSquare = MapSquare(mx, my)
            viewport.add(mapSquare)
        }
    }

    val hiddenMaps = isolation[mapSquare().id]
    if (hiddenMaps != null) {
        viewport.removeIf { it.id in hiddenMaps }
    }

    return viewport
}
