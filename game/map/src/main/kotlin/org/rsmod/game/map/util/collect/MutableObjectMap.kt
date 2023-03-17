package org.rsmod.game.map.util.collect

import it.unimi.dsi.fastutil.bytes.Byte2IntArrayMap
import it.unimi.dsi.fastutil.bytes.Byte2IntMap
import it.unimi.dsi.fastutil.bytes.Byte2IntMaps

@JvmInline
public value class MutableObjectMap(private val backing: Byte2IntMap) {

    public val size: Int get() = backing.size

    public operator fun set(key: Byte, value: Int) {
        backing[key] = value
    }

    public operator fun get(key: Byte): Int? {
        if (!backing.containsKey(key)) return null
        return backing.get(key)
    }

    public fun remove(key: Byte): Int? {
        if (!backing.containsKey(key)) return null
        return backing.remove(key)
    }

    public fun entrySet(): Set<Byte2IntMap.Entry> = backing.byte2IntEntrySet()

    public fun immutable(): ImmutableObjectMap {
        return ImmutableObjectMap(Byte2IntMaps.unmodifiable(backing))
    }

    public companion object {

        public fun empty(capacity: Int? = null): MutableObjectMap {
            val backing = capacity?.let { Byte2IntArrayMap(capacity) } ?: Byte2IntArrayMap()
            return MutableObjectMap(backing)
        }

        public fun unmodifiable(): MutableObjectMap {
            val backing = Byte2IntArrayMap(0)
            return MutableObjectMap(Byte2IntMaps.unmodifiable(backing))
        }
    }
}
