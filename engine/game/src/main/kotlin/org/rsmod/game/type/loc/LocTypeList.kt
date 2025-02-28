package org.rsmod.game.type.loc

import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.type.TypeResolver

public data class LocTypeList(public val types: MutableMap<Int, UnpackedLocType>) :
    Map<Int, UnpackedLocType> by types {
    public operator fun get(type: LocType): UnpackedLocType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")

    public operator fun get(loc: LocInfo): UnpackedLocType =
        types[loc.id] ?: throw NoSuchElementException("Type is missing in the map: $loc.")

    public operator fun get(loc: BoundLocInfo): UnpackedLocType =
        types[loc.id] ?: throw NoSuchElementException("Type is missing in the map: $loc.")
}
