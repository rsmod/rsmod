package org.runerealm.game.type.loc

import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.loc.LocInfo
import org.runerealm.game.type.TypeResolver

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
