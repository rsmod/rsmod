package org.rsmod.game.region.util

import org.rsmod.map.util.Translation
import org.rsmod.map.zone.ZoneGrid

public object RegionRotations {
    private const val ZONE_LENGTH_EXCLUSIVE: Int = ZoneGrid.LENGTH - 1

    public fun translateLoc(
        regionRot: Int,
        locSwGrid: ZoneGrid,
        locAdjustedWidth: Int,
        locAdjustedLength: Int,
    ): Translation = translate(regionRot, locSwGrid, locAdjustedWidth, locAdjustedLength)

    public fun translateCoords(regionRot: Int, coordGrid: ZoneGrid): Translation =
        translate(regionRot, coordGrid, width = 1, length = 1)

    public fun translate(regionRot: Int, grid: ZoneGrid, width: Int, length: Int): Translation =
        rotate(regionRot, grid.x, grid.z, width, length)

    private fun rotate(
        regionRot: Int,
        gridX: Int,
        gridZ: Int,
        width: Int,
        length: Int,
    ): Translation {
        require(regionRot in 0..<4) { "Region rotation must be within range [0..3]." }
        val widthExcl = width - 1
        val lengthExcl = length - 1
        return when (regionRot) {
            1 -> {
                val rx = gridZ
                val rz = ZONE_LENGTH_EXCLUSIVE - gridX - widthExcl
                Translation(rx, rz)
            }
            2 -> {
                val rx = ZONE_LENGTH_EXCLUSIVE - gridX - widthExcl
                val rz = ZONE_LENGTH_EXCLUSIVE - gridZ - lengthExcl
                Translation(rx, rz)
            }
            3 -> {
                val rx = ZONE_LENGTH_EXCLUSIVE - gridZ - lengthExcl
                val rz = gridX
                Translation(rx, rz)
            }
            else -> Translation(gridX, gridZ)
        }
    }
}
