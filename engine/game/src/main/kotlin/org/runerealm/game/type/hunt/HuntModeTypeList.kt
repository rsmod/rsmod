package org.runerealm.game.type.hunt

import org.runerealm.game.type.TypeResolver

public data class HuntModeTypeList(public val types: MutableMap<Int, UnpackedHuntModeType>) :
    Map<Int, UnpackedHuntModeType> by types {
    public operator fun get(type: HuntModeType): UnpackedHuntModeType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
