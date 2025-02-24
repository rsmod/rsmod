package org.rsmod.game.type.util

import org.rsmod.game.type.param.ParamType

public class ParamMap(
    public val primitiveMap: Map<Int, Any>,
    public var typedMap: Map<Int, Any?>? = null,
) : Iterable<Map.Entry<Int, Any?>> {
    public val checkedTypedMap: Map<Int, Any?>
        get() = typedMap ?: error("`typedMap` must be initialised.")

    public val keys: Set<Int>
        get() = checkedTypedMap.keys

    public val values: Collection<Any?>
        get() = checkedTypedMap.values

    public val entries: Set<Map.Entry<Int, Any?>>
        get() = checkedTypedMap.entries

    public fun isEmpty(): Boolean = checkedTypedMap.isEmpty()

    public fun isNotEmpty(): Boolean = !isEmpty()

    public operator fun <T : Any> contains(param: ParamType<T>): Boolean =
        checkedTypedMap.containsKey(param.id)

    @Suppress("UNCHECKED_CAST")
    public fun <T : Any> getOrNull(param: ParamType<T>): T? = checkedTypedMap[param.id] as? T

    public operator fun <T : Any> get(param: ParamType<T>): T? =
        getOrNull(param) ?: param.typedDefault

    public operator fun plus(other: ParamMap): ParamMap {
        val otherTypedMap = other.typedMap
        val thisTypedMap = typedMap
        val combinedTypedMap =
            when {
                thisTypedMap == null -> otherTypedMap
                otherTypedMap == null -> thisTypedMap
                else -> thisTypedMap + otherTypedMap
            }
        val combinedPrimitiveMap = primitiveMap + other.primitiveMap
        return ParamMap(combinedPrimitiveMap, combinedTypedMap)
    }

    override fun iterator(): Iterator<Map.Entry<Int, Any?>> = checkedTypedMap.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParamMap) return false
        if (primitiveMap != other.primitiveMap) return false
        return true
    }

    override fun hashCode(): Int = primitiveMap.entries.hashCode()

    override fun toString(): String = typedMap?.toString() ?: primitiveMap.toString()
}

public fun <T : Any> ParamMap?.resolve(param: ParamType<T>): T {
    if (this == null) {
        val default = param.typedDefault
        checkNotNull(default) {
            "Param `$param` does not have a default value. Use `paramOrNull` instead."
        }
        return default
    }

    val value = this[param]
    if (value != null) {
        return value
    }

    val default = param.typedDefault
    checkNotNull(default) {
        "Param `$param` does not have a default value. Use `paramOrNull` instead."
    }
    return default
}
