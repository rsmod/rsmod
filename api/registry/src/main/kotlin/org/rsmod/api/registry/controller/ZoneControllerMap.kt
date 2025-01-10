package org.rsmod.api.registry.controller

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.rsmod.map.zone.ZoneKey

@JvmInline
public value class ZoneControllerMap(private val entries: Int2ObjectMap<ZoneControllerEntryList>) {
    public val activeZoneCount: Int
        get() = entries.size

    public constructor() : this(Int2ObjectOpenHashMap())

    public fun controllerCount(): Int = entries.values.sumOf { it.size }

    public fun getOrPut(key: ZoneKey): ZoneControllerEntryList =
        entries.computeIfAbsent(key.packed) { ZoneControllerEntryList() }

    public operator fun get(key: ZoneKey): ZoneControllerEntryList? =
        entries.getOrDefault(key.packed, null)
}
