package org.rsmod.game.area.polygon

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import it.unimi.dsi.fastutil.shorts.ShortSet
import java.util.BitSet
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.util.LocalMapSquareZone

public class PolygonMapSquare(
    public val coordAreas: Short2ObjectMap<BitSet>,
    public val zoneAreas: Short2ObjectMap<BitSet>,
    public val mapSquareAreas: ShortSet,
) {
    public fun containsArea(area: Short, tile: MapSquareGrid): Boolean {
        val bits = coordAreas[area] ?: return false
        return bits.get(tile.packed)
    }

    public fun containsArea(area: Short, zone: LocalMapSquareZone): Boolean {
        val bits = zoneAreas[area] ?: return false
        return bits.get(zone.packed)
    }

    public fun hasMapSquareArea(area: Short): Boolean = mapSquareAreas.contains(area)

    public fun hasCoordArea(area: Short): Boolean = coordAreas.containsKey(area)

    public fun hasZoneArea(area: Short): Boolean = zoneAreas.containsKey(area)
}
