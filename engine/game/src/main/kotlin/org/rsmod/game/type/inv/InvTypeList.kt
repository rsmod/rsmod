package org.rsmod.game.type.inv

import org.rsmod.game.type.TypeResolver

public data class InvTypeList(public val types: MutableMap<Int, UnpackedInvType>) :
    Map<Int, UnpackedInvType> by types {
    public operator fun get(type: InvType): UnpackedInvType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
