package org.runerealm.game.type.hitmark

import org.runerealm.game.type.TypeResolver

public data class HitmarkTypeList(public val types: MutableMap<Int, UnpackedHitmarkType>) :
    Map<Int, UnpackedHitmarkType> by types {
    public operator fun get(type: HitmarkType): UnpackedHitmarkType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
