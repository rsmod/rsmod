package org.rsmod.plugins.api.cache.type.enums

import org.rsmod.plugins.types.NamedEnum

public class EnumTypeList(private val elements: Map<Int, EnumType<Any, Any>>) {

    public val size: Int get() = elements.size

    public val values: Iterable<EnumType<Any, Any>> get() = elements.values

    @Suppress("UNCHECKED_CAST")
    public fun <K, V> get(enum: NamedEnum, input: Class<K>, output: Class<V>): EnumType<K, V> {
        val element = elements[enum.id] ?: throw NullPointerException("Enum with id `${enum.id}` not mapped.")
        checkTypes(input, element.keyType.out, "input")
        checkTypes(output, element.valType.out, "output")
        return element as EnumType<K, V>
    }

    public fun getValue(id: Int): EnumType<Any, Any> {
        return elements.getValue(id)
    }

    private fun <T1, T2> checkTypes(received: Class<T1>, expected: Class<T2>, io: String) {
        check(received == expected) {
            "Incorrect $io type. (expected=${expected.simpleName}, received=${received.simpleName})"
        }
    }
}
