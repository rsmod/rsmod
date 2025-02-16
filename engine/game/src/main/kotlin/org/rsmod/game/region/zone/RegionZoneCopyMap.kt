package org.rsmod.game.region.zone

import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.rsmod.map.zone.ZoneKey

@JvmInline
public value class RegionZoneCopyMap(public val backing: Int2IntMap = Int2IntOpenHashMap()) {
    public operator fun get(regionZone: ZoneKey): RegionZoneCopy {
        val regionKey = backing[regionZone.packed]
        return if (regionKey == backing.defaultReturnValue()) {
            RegionZoneCopy.NULL
        } else {
            RegionZoneCopy(regionKey)
        }
    }

    public operator fun set(regionZone: ZoneKey, copyZone: RegionZoneCopy) {
        backing[regionZone.packed] = copyZone.packed
    }

    public operator fun contains(regionZone: ZoneKey): Boolean =
        backing.containsKey(regionZone.packed)

    public fun remove(regionZone: ZoneKey): Boolean {
        val removed = backing.remove(regionZone.packed)
        return removed != backing.defaultReturnValue()
    }

    public fun translate(base: ZoneKey): RegionZoneCopyMap {
        val translatedZones = Int2IntOpenHashMap()
        for ((packedRegionZone, packedCopyZone) in backing) {
            val regionZone = ZoneKey(packedRegionZone)
            val translated = regionZone.translate(base.x, base.z)
            translatedZones[translated.packed] = packedCopyZone
        }
        return RegionZoneCopyMap(translatedZones)
    }
}
