package org.rsmod.api.cache.map.npc

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

public data class MapNpcListDefinition(public val packedSpawns: IntList) {
    public companion object {
        public fun merge(
            edit: MapNpcListDefinition,
            base: MapNpcListDefinition,
        ): MapNpcListDefinition {
            val merged = IntArrayList(base.packedSpawns.size + edit.packedSpawns.size)
            merged.addAll(base.packedSpawns)
            merged.addAll(edit.packedSpawns)
            return MapNpcListDefinition(merged)
        }
    }
}
