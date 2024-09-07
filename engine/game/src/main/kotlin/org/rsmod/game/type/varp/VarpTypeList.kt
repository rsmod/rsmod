package org.rsmod.game.type.varp

import org.rsmod.game.type.TypeResolver

public data class VarpTypeList(public val types: MutableMap<Int, UnpackedVarpType>) :
    Map<Int, UnpackedVarpType> by types {
    public operator fun get(type: VarpType): UnpackedVarpType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
