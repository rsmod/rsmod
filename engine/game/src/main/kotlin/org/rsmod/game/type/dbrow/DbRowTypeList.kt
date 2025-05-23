package org.rsmod.game.type.dbrow

import org.rsmod.game.type.TypeResolver

public data class DbRowTypeList(public val types: MutableMap<Int, UnpackedDbRowType>) :
    Map<Int, UnpackedDbRowType> by types {
    public operator fun get(type: DbRowType): UnpackedDbRowType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
