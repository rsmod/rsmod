package org.rsmod.api.cache.map.tile

import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid

public data class MapTileDefinition(
    public val tileHeights: Map<MapSquareGrid, Int>,
    public val rules: Map<MapSquareGrid, Byte>,
    public val overlays: Map<MapSquareGrid, TileOverlay>,
    public val underlays: Map<MapSquareGrid, TileUnderlay>,
) {
    public companion object {
        public const val BLOCK_MAP_SQUARE: Int = 0x1
        public const val LINK_BELOW: Int = 0x2
        public const val REMOVE_ROOFS: Int = 0x4
        public const val VISIBLE_BELOW: Int = 0x8
        public const val NOT_LOW_DETAIL: Int = 0x10
    }
}

public class MapTileSimpleDefinition(private val packed: ByteArray = ByteArray(TOTAL_SIZE)) {
    public operator fun set(x: Int, z: Int, level: Int, flag: Int) {
        val index = (z and 0x3F) or ((x and 0x3F) shl 6) or ((level and 0x3) shl 12)
        packed[index] = (packed[index].toInt() or flag).toByte()
    }

    public operator fun get(x: Int, z: Int, level: Int): Byte {
        val index = (z and 0x3F) or ((x and 0x3F) shl 6) or ((level and 0x3) shl 12)
        return packed[index]
    }

    public companion object {
        public const val BLOCK_MAP_SQUARE: Int = 0x1
        public const val LINK_BELOW: Int = 0x2
        public const val REMOVE_ROOFS: Int = 0x4
        public const val COLOURED: Int = 0x8

        private const val TOTAL_SIZE: Int =
            CoordGrid.LEVEL_COUNT * MapSquareGrid.LENGTH * MapSquareGrid.LENGTH
    }
}
