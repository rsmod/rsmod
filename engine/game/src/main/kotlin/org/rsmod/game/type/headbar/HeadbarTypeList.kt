package org.rsmod.game.type.headbar

import org.rsmod.game.type.TypeResolver

public data class HeadbarTypeList(public val types: MutableMap<Int, UnpackedHeadbarType>) :
    Map<Int, UnpackedHeadbarType> by types {
    public operator fun get(type: HeadbarType): UnpackedHeadbarType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
