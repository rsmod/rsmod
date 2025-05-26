package org.rsmod.game.enums

import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.enums.EnumTypeList

public class EnumTypeMapResolver(private val types: EnumTypeList) {
    public operator fun <K : Any, V : Any> get(enum: EnumType<K, V>): EnumTypeMap<K, V> =
        EnumTypeMap(types[enum])
}
