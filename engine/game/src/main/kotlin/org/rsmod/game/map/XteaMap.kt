package org.rsmod.game.map

import org.rsmod.map.square.MapSquareKey

public data class XteaMap(public val key: Map<MapSquareKey, IntArray>) :
    Map<MapSquareKey, IntArray> by key
