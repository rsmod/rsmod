package org.rsmod.api.registry.npc

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.rsmod.map.zone.ZoneKey

@JvmInline
public value class ZoneNpcMap(private val entries: Int2ObjectMap<ZoneNpcEntryList>) {
    public val activeZoneCount: Int
        get() = entries.size

    public constructor() : this(Int2ObjectOpenHashMap())

    public fun npcCount(): Int = entries.values.sumOf { it.size }

    public fun getOrPut(key: ZoneKey): ZoneNpcEntryList =
        entries.computeIfAbsent(key.packed) { ZoneNpcEntryList() }

    public operator fun get(key: ZoneKey): ZoneNpcEntryList? =
        entries.getOrDefault(key.packed, null)
}
