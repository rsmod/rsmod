package org.runerealm.game.type.font

import org.runerealm.game.type.TypeResolver

public class FontMetricsTypeList(public val types: Map<Int, UnpackedFontMetricsType>) :
    Map<Int, UnpackedFontMetricsType> by types {
    public operator fun get(type: FontMetricsType): UnpackedFontMetricsType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
