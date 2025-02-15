package org.rsmod.game.map

import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.loc.LocZoneKey
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey

@JvmInline
public value class ZoneLocMap(private val entries: Int2ObjectMap<Byte2IntOpenHashMap>) {
    public val zoneCount: Int
        get() = entries.size

    public constructor() : this(Int2ObjectOpenHashMap())

    public fun locCount(): Int = entries.values.sumOf { it.size }

    public fun build(key: ZoneKey, build: ZoneBuilder.() -> Unit) {
        val zone = entries.computeIfAbsent(key.packed) { Byte2IntOpenHashMap() }
        val builder = ZoneBuilder(zone)
        build(builder)
        check(zone.isNotEmpty()) { "Zone is empty: $key (${key.toCoords()})" }
    }

    public fun getOrPut(key: ZoneKey): Byte2IntOpenHashMap =
        entries.computeIfAbsent(key.packed) { Byte2IntOpenHashMap() }

    public operator fun get(key: ZoneKey): Byte2IntOpenHashMap? =
        entries.getOrDefault(key.packed, null)

    public operator fun get(zone: ZoneKey, locZoneKey: LocZoneKey): Int? {
        val map = entries.getOrDefault(zone.packed, null) ?: return null
        return map.getOrDefault(locZoneKey.packed, null)
    }

    public operator fun set(zone: ZoneKey, locZoneKey: LocZoneKey, locEntity: LocEntity) {
        val map = entries.computeIfAbsent(zone.packed) { Byte2IntOpenHashMap() }
        map[locZoneKey.packed] = locEntity.packed
    }

    /**
     * Should only be used when building large portions of a zone at a time, such as when the game
     * map is first being initialised.
     */
    public operator fun set(key: Int, zoneLocs: Byte2IntOpenHashMap) {
        check(!entries.containsKey(key)) { "Zone `$key` already set." }
        entries[key] = zoneLocs
    }

    public operator fun contains(loc: LocInfo): Boolean {
        val zoneKey = ZoneKey.Companion.from(loc.coords)
        val locKey = LocZoneKey(ZoneGrid.Companion.from(loc.coords), loc.layer)
        return this[zoneKey, locKey] == loc.entity.packed
    }

    public class ZoneBuilder(private val zone: Byte2IntOpenHashMap) {
        public operator fun set(key: LocZoneKey, entity: LocEntity) {
            zone[key.packed] = entity.packed
        }
    }
}
