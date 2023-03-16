package org.rsmod.plugins.api.cache.map

import org.rsmod.game.map.Coordinates
import org.rsmod.plugins.api.cache.map.tile.TileOverlay
import org.rsmod.plugins.api.cache.map.tile.TileUnderlay

public data class MapDefinition(
    public val tileHeights: Map<Coordinates, Int>,
    public val rules: Map<Coordinates, Byte>,
    public val overlays: Map<Coordinates, TileOverlay>,
    public val underlays: Map<Coordinates, TileUnderlay>
)
