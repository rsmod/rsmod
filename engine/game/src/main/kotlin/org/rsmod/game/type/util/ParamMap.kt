package org.rsmod.game.type.util

import org.rsmod.game.type.param.ParamType

public class ParamMap(
    public val primitiveMap: Map<Int, Any>,
    public var typedMap: Map<Int, Any>? = null,
) : Iterable<Map.Entry<Int, Any>> {
    public val checkedTypedMap: Map<Int, Any>
        get() = typedMap ?: error("`typedMap` must be initialised.")

    public val keys: Set<Int>
        get() = checkedTypedMap.keys

    public val values: Collection<Any>
        get() = checkedTypedMap.values

    public val entries: Set<Map.Entry<Int, Any>>
        get() = checkedTypedMap.entries

    public fun isEmpty(): Boolean = checkedTypedMap.isEmpty()

    public fun isNotEmpty(): Boolean = !isEmpty()

    @Suppress("UNCHECKED_CAST")
    public fun <T : Any> getOrNull(param: ParamType<T>): T? = checkedTypedMap[param.id] as? T

    public operator fun <T : Any> get(param: ParamType<T>): T? =
        getOrNull(param) ?: param.typedDefault

    public operator fun <T : Any> contains(param: ParamType<T>): Boolean =
        checkedTypedMap.containsKey(param.id)

    override fun iterator(): Iterator<Map.Entry<Int, Any>> = checkedTypedMap.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParamMap) return false
        if (primitiveMap != other.primitiveMap) return false
        return true
    }

    override fun hashCode(): Int = primitiveMap.entries.hashCode()

    override fun toString(): String = typedMap?.toString() ?: primitiveMap.toString()
}
