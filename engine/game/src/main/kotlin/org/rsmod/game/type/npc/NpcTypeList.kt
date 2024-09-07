package org.rsmod.game.type.npc

import org.rsmod.game.type.TypeResolver

public data class NpcTypeList(public val types: Map<Int, UnpackedNpcType>) :
    Map<Int, UnpackedNpcType> by types {
    public operator fun get(type: NpcType): UnpackedNpcType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
