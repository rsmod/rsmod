package org.rsmod.game.type.util

import org.rsmod.game.type.enums.UnpackedEnumType

public class EnumTypeMap<K : Any, V : Any>(type: UnpackedEnumType<K, V>) :
    Iterable<Map.Entry<K, V?>> {
    private val entries: Map<K, V?> = type.typedMap
    internal val default: V? = type.default

    public val isEmpty: Boolean
        get() = entries.isEmpty()

    public val isNotEmpty: Boolean
        get() = entries.isNotEmpty()

    public val keys: Set<K>
        get() = entries.keys

    public val values: Collection<V?>
        get() = entries.values

    public fun getValue(key: K): V =
        this[key] ?: throw NoSuchElementException("Key $key is missing in the map.")

    public fun getOrNull(key: K): V? = entries[key]

    public operator fun get(key: K): V? = entries[key] ?: default

    public operator fun contains(key: K): Boolean = entries.containsKey(key)

    override fun iterator(): Iterator<Map.Entry<K, V?>> = entries.iterator()

    /**
     * Creates a new [EnumTypeNonNullMap] containing only the entries with non-null values.
     *
     * _Note: This function does not consider the [UnpackedEnumType.default] value of the associated
     * enum type. Even if the `default` is non-null, any key-value entry with a null value will
     * still be excluded from the result._
     */
    public fun filterValuesNotNull(): EnumTypeNonNullMap<K, V> {
        return EnumTypeNonNullMap.from(this)
    }
}

public class EnumTypeNonNullMap<K : Any, V : Any>(
    private val entries: Map<K, V>,
    private val default: V?,
) : Iterable<Map.Entry<K, V>> {
    public val isEmpty: Boolean
        get() = entries.isEmpty()

    public val isNotEmpty: Boolean
        get() = entries.isNotEmpty()

    public val keys: Set<K>
        get() = entries.keys

    public val values: Collection<V>
        get() = entries.values

    public fun getOrNull(key: K): V? = entries[key]

    public operator fun get(key: K): V? =
        entries[key] ?: default ?: throw NoSuchElementException("Key $key is missing in the map.")

    public operator fun contains(key: K): Boolean = entries.containsKey(key)

    override fun iterator(): Iterator<Map.Entry<K, V>> = entries.iterator()

    public companion object {
        public fun <K : Any, V : Any> from(other: EnumTypeMap<K, V>): EnumTypeNonNullMap<K, V> {
            val filteredEntries = mutableMapOf<K, V>()
            for ((key, value) in other) {
                if (value != null) {
                    filteredEntries[key] = value
                }
            }
            return EnumTypeNonNullMap(filteredEntries, other.default)
        }
    }
}
