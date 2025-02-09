package org.rsmod.api.utils.map

import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey

public object BuildAreaUtils {
    public const val SIZE: Int = 104

    // Rebuilds when you step into 2-zone boundary
    public const val REBUILD_BOUNDARY: Int = ZoneGrid.LENGTH * 2

    public const val ZONE_VIEW_RADIUS: Int = SIZE / REBUILD_BOUNDARY

    public fun calculateBuildArea(zone: ZoneKey): CoordGrid {
        val baseZone = zone.translate(-ZONE_VIEW_RADIUS, -ZONE_VIEW_RADIUS)
        return baseZone.toCoords()
    }

    public fun isOutsideOfBuildArea(
        coords: CoordGrid,
        buildArea: CoordGrid,
        size: Int = SIZE,
    ): Boolean {
        val dx = coords.x - buildArea.x
        val dz = coords.z - buildArea.z
        return dx < REBUILD_BOUNDARY ||
            dz < REBUILD_BOUNDARY ||
            dx >= size - REBUILD_BOUNDARY ||
            dz >= size - REBUILD_BOUNDARY
    }

    public fun requiresNewBuildArea(player: Player): Boolean =
        isOutsideOfBuildArea(player.coords, player.buildArea)
}
