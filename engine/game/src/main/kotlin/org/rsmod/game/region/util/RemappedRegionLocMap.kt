package org.rsmod.game.region.util

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
import org.rsmod.map.CoordGrid

public class RemappedRegionLocMap(public val backing: Long2IntOpenHashMap = Long2IntOpenHashMap()) {
    public operator fun get(regionCoords: CoordGrid, loc: Int): CoordGrid {
        val key = RemappedRegionLocKey(regionCoords, loc)
        val mapped = backing[key.packed]
        return if (mapped == backing.defaultReturnValue()) {
            CoordGrid.NULL
        } else {
            CoordGrid(mapped)
        }
    }

    public operator fun set(regionCoords: CoordGrid, loc: Int, normalCoords: CoordGrid) {
        val key = RemappedRegionLocKey(regionCoords, loc)
        backing[key.packed] = normalCoords.packed
    }

    @JvmInline
    private value class RemappedRegionLocKey(val packed: Long) {
        constructor(regionCoords: CoordGrid, loc: Int) : this(pack(regionCoords, loc))

        companion object {
            private const val COORD_BIT_COUNT: Int = 30
            private const val LOC_ID_BIT_COUNT: Int = 17

            private const val COORD_BIT_OFFSET: Int = 0
            private const val LOC_ID_BIT_OFFSET: Int = COORD_BIT_OFFSET + COORD_BIT_COUNT

            private const val COORD_BIT_MASK: Long = (1L shl COORD_BIT_COUNT) - 1
            private const val LOC_ID_BIT_MASK: Long = (1L shl LOC_ID_BIT_COUNT) - 1

            private fun pack(coords: CoordGrid, loc: Int): Long {
                require(loc in 0..LOC_ID_BIT_MASK) {
                    "`loc` must be within range [0-$LOC_ID_BIT_MASK]."
                }
                return ((coords.packed.toLong() and COORD_BIT_MASK) shl COORD_BIT_OFFSET) or
                    ((loc.toLong() and LOC_ID_BIT_MASK) shl LOC_ID_BIT_OFFSET)
            }
        }
    }
}
