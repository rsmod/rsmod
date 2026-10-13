package org.runerealm.game.type.struct

import org.runerealm.game.type.TypeResolver

public data class StructTypeList(public val types: Map<Int, UnpackedStructType>) :
    Map<Int, UnpackedStructType> by types {
    public operator fun get(type: StructType): UnpackedStructType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
