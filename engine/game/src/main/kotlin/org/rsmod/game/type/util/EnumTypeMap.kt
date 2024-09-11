package org.rsmod.game.type.util

import org.rsmod.game.type.enums.UnpackedEnumType

public class EnumTypeMap<K : Any, V : Any>(type: UnpackedEnumType<K, V>) :
    Iterable<Map.Entry<K, V?>> {
    private val entries: Map<K, V?> = type.typedMap
    private val default: V? = type.default

    public fun getValue(key: K): V =
        this[key] ?: throw NoSuchElementException("Key $key is missing in the map.")

    public fun getOrNull(key: K): V? = entries[key]

    public operator fun get(key: K): V? = entries[key] ?: default

    public operator fun contains(key: K): Boolean = entries.containsKey(key)

    override fun iterator(): Iterator<Map.Entry<K, V?>> = entries.iterator()
}
