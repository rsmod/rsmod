package org.rsmod.api.cache.types.jingle

import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.jingle.JingleType
import org.rsmod.game.type.jingle.JingleTypeList

public object JingleTypeDecoder {
    public fun decodeAll(nameMapping: NameMapping): JingleTypeList =
        JingleTypeList(nameMapping.toTypeMap())

    private fun NameMapping.toTypeMap(): Map<Int, JingleType> =
        jingles.entries.associate { it.value to JingleType(it.value, it.key) }
}
