package org.rsmod.api.registry.player

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.rsmod.map.zone.ZoneKey

@JvmInline
public value class ZonePlayerMap(private val entries: Int2ObjectMap<ZonePlayerEntryList>) {
    public val activeZoneCount: Int
        get() = entries.size

    public constructor() : this(Int2ObjectOpenHashMap())

    public fun playerCount(): Int = entries.values.sumOf { it.size }

    public fun getOrPut(key: ZoneKey): ZonePlayerEntryList =
        entries.computeIfAbsent(key.packed) { ZonePlayerEntryList() }

    public operator fun get(key: ZoneKey): ZonePlayerEntryList? =
        entries.getOrDefault(key.packed, null)
}
