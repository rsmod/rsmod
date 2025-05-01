package org.rsmod.game.type.area

import org.rsmod.game.type.TypeResolver

public data class AreaTypeList(public val types: MutableMap<Int, UnpackedAreaType>) :
    Map<Int, UnpackedAreaType> by types {
    public operator fun get(type: AreaType): UnpackedAreaType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
