package org.rsmod.game.type.proj

import org.rsmod.game.type.TypeResolver

public data class ProjAnimTypeList(public val types: MutableMap<Int, UnpackedProjAnimType>) :
    Map<Int, UnpackedProjAnimType> by types {
    public operator fun get(type: ProjAnimType): UnpackedProjAnimType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
