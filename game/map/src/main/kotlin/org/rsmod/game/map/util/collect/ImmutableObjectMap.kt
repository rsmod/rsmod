package org.rsmod.game.map.util.collect

import it.unimi.dsi.fastutil.bytes.Byte2IntArrayMap
import it.unimi.dsi.fastutil.bytes.Byte2IntMap
import it.unimi.dsi.fastutil.bytes.Byte2IntMaps

@JvmInline
public value class ImmutableObjectMap(private val backing: Byte2IntMap) {

    public val size: Int get() = backing.size

    public operator fun get(key: Byte): Int? {
        if (!backing.containsKey(key)) return null
        return backing.get(key)
    }

    public fun entrySet(): Set<Byte2IntMap.Entry> = backing.byte2IntEntrySet()

    public companion object {

        public fun empty(capacity: Int? = null): ImmutableObjectMap {
            val backing = capacity?.let { Byte2IntArrayMap(capacity) } ?: Byte2IntArrayMap()
            return ImmutableObjectMap(Byte2IntMaps.unmodifiable(backing))
        }
    }
}
