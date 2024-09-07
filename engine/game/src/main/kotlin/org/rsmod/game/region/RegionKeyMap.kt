package org.rsmod.game.region

import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.rsmod.map.zone.ZoneKey

@JvmInline
public value class RegionKeyMap(private val regionKeys: Int2IntMap = Int2IntOpenHashMap()) {
    public operator fun get(zoneKey: ZoneKey): RegionZoneKey {
        val regionKey = regionKeys[zoneKey.packed]
        return if (regionKey == regionKeys.defaultReturnValue()) {
            RegionZoneKey.NULL
        } else {
            RegionZoneKey(regionKey)
        }
    }

    public operator fun set(normalKey: ZoneKey, regionKey: RegionZoneKey) {
        regionKeys[normalKey.packed] = regionKey.packed
    }
}
