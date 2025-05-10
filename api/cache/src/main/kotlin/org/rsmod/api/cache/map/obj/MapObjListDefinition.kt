package org.rsmod.api.cache.map.obj

import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongList

public data class MapObjListDefinition(public val packedSpawns: LongList) {
    public companion object {
        public fun merge(
            edit: MapObjListDefinition,
            base: MapObjListDefinition,
        ): MapObjListDefinition {
            val merged = LongArrayList(base.packedSpawns.size + edit.packedSpawns.size)
            merged.addAll(base.packedSpawns)
            merged.addAll(edit.packedSpawns)
            return MapObjListDefinition(merged)
        }
    }
}
