package org.rsmod.game.type.varbit

import org.rsmod.game.type.TypeResolver

public data class VarBitTypeList(public val types: MutableMap<Int, UnpackedVarBitType>) :
    Map<Int, UnpackedVarBitType> by types {
    public operator fun get(type: VarBitType): UnpackedVarBitType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
