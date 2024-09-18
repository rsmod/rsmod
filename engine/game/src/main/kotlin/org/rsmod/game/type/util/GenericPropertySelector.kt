package org.rsmod.game.type.util

public object GenericPropertySelector {
    public fun <T> selectPredicate(priority: T, other: T, selectPriority: () -> Boolean): T =
        if (selectPriority()) priority else other

    /**
     * @return [priority]'s [getValue] value _only if_ retrieved value does not equal [default].
     *   Otherwise, returns [other]'s [getValue] value.
     */
    public fun <T, V> select(priority: T, other: T, default: V?, getValue: T.() -> V): V {
        val selected = selectPredicate(priority, other) { getValue(priority) != default }
        return getValue(selected)
    }

    public fun <T, V> select(priority: T, other: T, getValue: T.() -> Array<V>): Array<V> {
        val selected = selectPredicate(priority, other) { getValue(priority).isNotEmpty() }
        return getValue(selected)
    }

    public fun <T> selectIntArray(priority: T, other: T, getValue: T.() -> IntArray): IntArray {
        val selected = selectPredicate(priority, other) { getValue(priority).isNotEmpty() }
        return getValue(selected)
    }

    public fun <T> selectShortArray(priority: T, other: T, getVal: T.() -> ShortArray): ShortArray {
        val selected = selectPredicate(priority, other) { getVal(priority).isNotEmpty() }
        return getVal(selected)
    }

    public fun <T> selectByteArray(priority: T, other: T, getValue: T.() -> ByteArray): ByteArray {
        val selected = selectPredicate(priority, other) { getValue(priority).isNotEmpty() }
        return getValue(selected)
    }

    public fun <T, K, V> selectMap(priority: T, other: T, getValue: T.() -> Map<K, V>): Map<K, V> {
        val selected = selectPredicate(priority, other) { getValue(priority).isNotEmpty() }
        return getValue(selected)
    }

    public fun <T> selectParamMap(priority: T, other: T, getValue: T.() -> ParamMap?): ParamMap? {
        val priorityParamMap = getValue(priority)
        val otherParamMap = getValue(other)
        return when {
            priorityParamMap != null && otherParamMap != null -> priorityParamMap + otherParamMap
            priorityParamMap != null -> priorityParamMap
            else -> otherParamMap
        }
    }
}
