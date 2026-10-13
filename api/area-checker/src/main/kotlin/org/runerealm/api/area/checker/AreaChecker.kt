package org.runerealm.api.area.checker

import it.unimi.dsi.fastutil.shorts.ShortArrayList
import jakarta.inject.Inject
import org.runerealm.api.registry.region.RegionRegistry
import org.runerealm.game.area.AreaIndex
import org.runerealm.game.type.area.AreaType
import org.runerealm.map.CoordGrid

public class AreaChecker
@Inject
constructor(private val areaIndex: AreaIndex, private val regions: RegionRegistry) {
    private val areaBuffer = ShortArrayList()

    public fun inArea(area: AreaType, coords: CoordGrid): Boolean {
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
