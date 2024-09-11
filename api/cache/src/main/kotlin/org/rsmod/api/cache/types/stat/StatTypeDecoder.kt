package org.rsmod.api.cache.types.stat

import org.rsmod.api.type.symbols.name.NameMapping
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.stat.StatTypeList

public object StatTypeDecoder {
    public fun decodeAll(nameMapping: NameMapping): StatTypeList =
        StatTypeList(nameMapping.toTypeMap())

    private fun NameMapping.toTypeMap(): Map<Int, StatType> =
        stats.entries.associate { it.value to StatType(it.value, it.key) }
}
