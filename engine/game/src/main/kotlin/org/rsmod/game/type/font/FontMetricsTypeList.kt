package org.rsmod.game.type.font

import org.rsmod.game.type.TypeResolver

public class FontMetricsTypeList(public val types: MutableMap<Int, UnpackedFontMetricsType>) :
    Map<Int, UnpackedFontMetricsType> by types {
    public operator fun get(type: FontMetricsType): UnpackedFontMetricsType =
        types[TypeResolver[type]]
            ?: throw NoSuchElementException("Type is missing in the map: $type.")
}
