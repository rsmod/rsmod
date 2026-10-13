package org.runerealm.game.enums

import org.runerealm.game.type.enums.EnumType
import org.runerealm.game.type.enums.EnumTypeList

public class EnumTypeMapResolver(private val types: EnumTypeList) {
    public operator fun <K : Any, V : Any> get(enum: EnumType<K, V>): EnumTypeMap<K, V> =
        EnumTypeMap(types[enum])
}
