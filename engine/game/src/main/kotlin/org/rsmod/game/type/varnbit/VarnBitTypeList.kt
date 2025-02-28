package org.rsmod.game.type.varnbit

import org.rsmod.game.type.TypeResolver

public data class VarnBitTypeList(public val types: MutableMap<Int, UnpackedVarnBitType>) :
    Map<Int, UnpackedVarnBitType> by types {
    public operator fun get(type: VarnBitType): UnpackedVarnBitType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
