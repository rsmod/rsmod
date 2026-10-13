package org.runerealm.game.type.varn

import org.runerealm.game.type.TypeResolver

public data class VarnTypeList(public val types: MutableMap<Int, UnpackedVarnType>) :
    Map<Int, UnpackedVarnType> by types {
    public operator fun get(type: VarnType): UnpackedVarnType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
