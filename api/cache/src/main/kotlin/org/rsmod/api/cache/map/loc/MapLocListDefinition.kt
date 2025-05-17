package org.rsmod.api.cache.map.loc

import it.unimi.dsi.fastutil.longs.LongArrayList

public data class MapLocListDefinition(public val spawns: LongArrayList) {
    public companion object {
        public fun merge(
            edit: MapLocListDefinition,
            base: MapLocListDefinition,
        ): MapLocListDefinition {
            val merged = LongArrayList(base.spawns.size + edit.spawns.size)
            merged.addAll(base.spawns)
            merged.addAll(edit.spawns)
            return MapLocListDefinition(merged)
        }
    }
}
