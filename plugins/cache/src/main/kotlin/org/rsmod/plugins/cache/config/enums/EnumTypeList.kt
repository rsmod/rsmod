package org.rsmod.plugins.cache.config.enums

import org.rsmod.plugins.types.NamedEnum

public class EnumTypeList(
    private val elements: Map<Int, EnumType<Any, Any>>
) : Map<Int, EnumType<Any, Any>> by elements {

    @Suppress("UNCHECKED_CAST")
    public operator fun <K, V> get(named: NamedEnum<K, V>): EnumType<K, V> {
        val element = elements[named.id] ?: throw NullPointerException("Enum with id `${named.id}` not mapped.")
        // TODO: come up with a way to make sure K,V signature match element's.
        // under normal circumstances, we could store the classes in NamedEnum,
        // but due to the restrictions of (current) value classes - this is not
        // an option.
        return element as EnumType<K, V>
    }
}
