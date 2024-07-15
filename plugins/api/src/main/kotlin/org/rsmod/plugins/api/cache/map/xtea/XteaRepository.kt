package org.rsmod.plugins.api.cache.map.xtea

import org.openrs2.crypto.SymmetricKey
import org.rsmod.game.map.square.MapSquareKey

public class XteaRepository(
    private val xtea: MutableMap<MapSquareKey, SymmetricKey> = mutableMapOf()
) : Map<MapSquareKey, SymmetricKey> by xtea {

    public operator fun set(mapSquare: MapSquareKey, key: SymmetricKey) {
        check(!xtea.containsKey(mapSquare)) { "XTEA already set for MapSquare (mapSquare=$mapSquare)." }
        xtea[mapSquare] = key
    }

    public operator fun set(mapSquareId: Int, key: SymmetricKey): Unit = set(MapSquareKey(mapSquareId), key)
}
