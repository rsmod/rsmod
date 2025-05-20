package org.rsmod.api.area.checker

import it.unimi.dsi.fastutil.shorts.ShortArrayList
import jakarta.inject.Inject
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.game.area.AreaIndex
import org.rsmod.game.type.area.AreaType
import org.rsmod.map.CoordGrid

public class AreaChecker
@Inject
constructor(private val areaIndex: AreaIndex, private val regions: RegionRegistry) {
    private val areaBuffer = ShortArrayList()

    public fun inArea(coords: CoordGrid, area: AreaType): Boolean {
        areaBuffer.clear()
        val normalized = coords.normalized()
        areaIndex.putAreas(normalized, areaBuffer)
        return areaBuffer.any { it.toInt() == area.id }
    }

    private fun CoordGrid.normalized(): CoordGrid =
        if (RegionRegistry.inWorkingArea(this)) {
            regions.normalizeCoords(this)
        } else {
            this
        }
}
