package org.rsmod.game.region.util

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.rsmod.map.CoordGrid

public class RemappedRegionLocMap(public val backing: Int2IntOpenHashMap = Int2IntOpenHashMap()) {
    public operator fun get(regionCoords: CoordGrid): CoordGrid {
        val mapped = backing[regionCoords.packed]
        return if (mapped == backing.defaultReturnValue()) {
            CoordGrid.NULL
        } else {
            CoordGrid(mapped)
        }
    }

    public operator fun set(regionCoords: CoordGrid, normalCoords: CoordGrid) {
        backing[regionCoords.packed] = normalCoords.packed
    }
}
