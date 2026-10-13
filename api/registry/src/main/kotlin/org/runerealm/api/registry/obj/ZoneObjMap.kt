package org.runerealm.api.registry.obj

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.runerealm.map.zone.ZoneKey

@JvmInline
public value class ZoneObjMap(public val entries: Int2ObjectMap<ObjStackEntryList>) {
    public val activeZoneCount: Int
        get() = entries.size

    public constructor() : this(Int2ObjectOpenHashMap())

    public fun objCount(): Int = entries.values.sumOf { it.entries.size }

    public fun getOrPut(key: ZoneKey): ObjStackEntryList =
        entries.computeIfAbsent(key.packed) { ObjStackEntryList() }

    public operator fun get(key: ZoneKey): ObjStackEntryList? =
        entries.getOrDefault(key.packed, null)
}
