package org.rsmod.plugins.api.cache.map.xtea

import org.openrs2.crypto.XteaKey
import org.rsmod.game.model.map.MapSquare

public class XteaRepository(
    private val xtea: MutableMap<MapSquare, XteaKey> = mutableMapOf()
) : Map<MapSquare, XteaKey> by xtea {

    public operator fun set(mapSquare: MapSquare, key: XteaKey) {
        check(!xtea.containsKey(mapSquare)) { "XTEA already set for MapSquare (mapSquare=$mapSquare)." }
        xtea[mapSquare] = key
    }

    public operator fun set(mapSquareId: Int, key: XteaKey): Unit = set(MapSquare(mapSquareId), key)
}
