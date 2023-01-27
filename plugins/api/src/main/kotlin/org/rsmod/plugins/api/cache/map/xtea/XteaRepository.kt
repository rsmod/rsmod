package org.rsmod.plugins.api.cache.map.xtea

import org.openrs2.crypto.XteaKey

class XteaRepository(
    private val xtea: MutableMap<Int, XteaKey> = mutableMapOf()
) : Map<Int, XteaKey> by xtea {

    operator fun set(mapSquareId: Int, key: XteaKey) {
        check(!xtea.containsKey(mapSquareId)) { "XTEA already set for MapSquare (mapSquareId=$mapSquareId)." }
        xtea[mapSquareId] = key
    }
}
