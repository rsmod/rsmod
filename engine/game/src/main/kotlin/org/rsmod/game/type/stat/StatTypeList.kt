package org.rsmod.game.type.stat

import org.rsmod.game.type.TypeResolver

public class StatTypeList(public val types: Map<Int, UnpackedStatType>) :
    Map<Int, UnpackedStatType> by types {
    public operator fun get(type: StatType): UnpackedStatType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
