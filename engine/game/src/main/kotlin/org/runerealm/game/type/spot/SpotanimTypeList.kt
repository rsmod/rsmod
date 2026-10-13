package org.runerealm.game.type.spot

import org.runerealm.game.type.TypeResolver

public data class SpotanimTypeList(public val types: Map<Int, UnpackedSpotanimType>) :
    Map<Int, UnpackedSpotanimType> by types {
    public operator fun get(type: SpotanimType): UnpackedSpotanimType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
