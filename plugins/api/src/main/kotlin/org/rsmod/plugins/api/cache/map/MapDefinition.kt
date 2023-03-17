package org.rsmod.plugins.api.cache.map

import org.rsmod.game.map.util.I14Coordinates
import org.rsmod.plugins.api.cache.map.tile.TileOverlay
import org.rsmod.plugins.api.cache.map.tile.TileUnderlay

public data class MapDefinition(
    public val tileHeights: Map<I14Coordinates, Int>,
    public val rules: Map<I14Coordinates, Byte>,
    public val overlays: Map<I14Coordinates, TileOverlay>,
    public val underlays: Map<I14Coordinates, TileUnderlay>
) {

    public companion object {

        public const val BLOCKED_BIT_FLAG: Int = 0x1
        public const val LINK_BELOW_BIT_FLAG: Int = 0x2
        public const val REMOVE_ROOF_BIT_FLAG: Int = 0x4
        public const val VISIBLE_BELOW_BIT_FLAG: Int = 0x8
        public const val FORCE_HIGH_DETAIL_BIT_FLAG: Int = 0x10
    }
}
