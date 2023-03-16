package org.rsmod.game.map.util.collect

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import org.rsmod.game.map.zone.Zone

@JvmInline
public value class MutableZoneMap(private val backing: Int2ObjectMap<Zone>) {

    public operator fun set(key: Int, value: Zone) {
        backing[key] = value
    }

    public operator fun get(key: Int): Zone? {
        if (!backing.containsKey(key)) return null
        return backing.get(key)
    }

    public fun getValue(key: Int): Zone {
        return this[key] ?: throw NoSuchElementException("Key $key is missing in the map.")
    }

    public fun remove(key: Int): Zone? {
        if (!backing.containsKey(key)) return null
        return backing.remove(key)
    }

    public fun entrySet(): Set<Int2ObjectMap.Entry<Zone>> {
        return backing.int2ObjectEntrySet()
    }

    public fun immutable(): ImmutableZoneMap {
        return ImmutableZoneMap(Int2ObjectMaps.unmodifiable(backing))
    }

    public companion object {

        public fun empty(capacity: Int? = null): MutableZoneMap {
            val backing = capacity?.let { Int2ObjectArrayMap<Zone>(capacity) } ?: Int2ObjectArrayMap<Zone>()
            return MutableZoneMap(backing)
        }
    }
}
