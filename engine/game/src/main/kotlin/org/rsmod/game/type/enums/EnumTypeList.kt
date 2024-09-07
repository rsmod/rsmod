package org.rsmod.game.type.enums

public data class EnumTypeList(public val types: Map<Int, UnpackedEnumType<Any, Any>>) :
    Map<Int, UnpackedEnumType<Any, Any>> by types {
    @Suppress("UNCHECKED_CAST")
    public operator fun <K : Any, V : Any> get(type: EnumType<K, V>): UnpackedEnumType<K, V> {
        val mapped =
            types[type.id] ?: throw NoSuchElementException("Type is missing in the map: $type.")
        return mapped as UnpackedEnumType<K, V>
    }
}
