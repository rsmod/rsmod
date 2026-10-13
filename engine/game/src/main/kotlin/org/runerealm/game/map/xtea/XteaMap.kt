package org.runerealm.game.map.xtea

import org.runerealm.map.square.MapSquareKey

public data class XteaMap(public val key: Map<MapSquareKey, IntArray>) :
    Map<MapSquareKey, IntArray> by key
