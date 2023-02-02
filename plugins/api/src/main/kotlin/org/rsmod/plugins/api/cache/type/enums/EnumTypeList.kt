package org.rsmod.plugins.api.cache.type.enums

import org.rsmod.game.types.NamedEnum

public class EnumTypeList(private val elements: Array<EnumType<Any, Any>?>) {

    public val capacity: Int get() = elements.size

    @Suppress("UNCHECKED_CAST")
    public fun <K, V> get(enum: NamedEnum, input: Class<K>, output: Class<V>): EnumType<K, V> {
        check(enum.id in elements.indices)
        checkNotNull(elements[enum.id])
        val element = elements[enum.id]!!
        checkTypes(input, element.keyType.out, "input")
        checkTypes(output, element.valType.out, "output")
        return elements[enum.id] as EnumType<K, V>
    }

    private fun <T1, T2> checkTypes(received: Class<T1>, expected: Class<T2>, io: String) {
        check(received == expected) {
            "Incorrect $io type. (expected=${expected.simpleName}, received=${received.simpleName})"
        }
    }
}
