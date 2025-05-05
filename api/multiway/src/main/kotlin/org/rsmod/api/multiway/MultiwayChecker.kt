package org.rsmod.api.multiway

import it.unimi.dsi.fastutil.shorts.ShortArrayList
import jakarta.inject.Inject
import org.rsmod.api.config.refs.areas
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.game.area.AreaIndex
import org.rsmod.game.entity.PathingEntity
import org.rsmod.map.CoordGrid

public class MultiwayChecker
@Inject
constructor(private val areaIndex: AreaIndex, private val regions: RegionRegistry) {
    private val areaBuffer = ShortArrayList()

    public operator fun contains(entity: PathingEntity): Boolean = contains(entity.coords)

    public operator fun contains(coords: CoordGrid): Boolean {
        areaBuffer.clear()

        val normalized = coords.normalized()
        areaIndex.putAreas(normalized, areaBuffer)
        return areaBuffer.any(::isMultiway)
    }

    // TODO: Decide if we really want to handle multiway area id comparison this way. I personally
    //  would prefer to have some flag in `AreaType` and then cache the first area we find with
    //  the flag set (i.e. `multiway` area). However, we know the original game stores very
    //  limited information per area (color + toggle flag).
    private fun isMultiway(area: Short): Boolean {
        return area.toInt() == areas.multiway.id
    }

    private fun CoordGrid.normalized(): CoordGrid =
        if (RegionRegistry.inWorkingArea(this)) {
            regions.normalizeCoords(this)
        } else {
            this
        }
}
