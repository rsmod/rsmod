package org.rsmod.game.region.util

import org.rsmod.map.util.Translation
import org.rsmod.map.zone.ZoneGrid

public object RegionRotations {
    public fun translateLoc(
        regionRot: Int,
        locSwGrid: ZoneGrid,
        locAdjustedWidth: Int,
        locAdjustedLength: Int,
    ): Translation = translate(regionRot, locSwGrid, locAdjustedWidth, locAdjustedLength)

    public fun translateCoords(regionRot: Int, coordGrid: ZoneGrid): Translation =
        translate(regionRot, coordGrid, width = 1, length = 1)

    public fun translate(regionRot: Int, grid: ZoneGrid, width: Int, length: Int): Translation =
        rotate(regionRot, grid.x, grid.z, ZoneGrid.LENGTH, ZoneGrid.LENGTH, width, length)

    public fun translateZone(
        regionRot: Int,
        zoneX: Int,
        zoneZ: Int,
        zoneWidth: Int,
        zoneLength: Int,
    ): Translation = rotate(regionRot, zoneX, zoneZ, zoneWidth, zoneLength, width = 1, length = 1)

    private fun rotate(
        regionRot: Int,
        gridX: Int,
        gridZ: Int,
        gridWidth: Int,
        gridLength: Int,
        width: Int,
        length: Int,
    ): Translation {
        require(regionRot in 0..<4) { "Region rotation must be within range [0..3]." }
        val widthExcl = width - 1
        val lengthExcl = length - 1
        val gridWidthExcl = gridWidth - 1
        val gridLengthExcl = gridLength - 1
        return when (regionRot) {
            1 -> {
                val rx = gridZ
                val rz = gridWidthExcl - gridX - widthExcl
                Translation(rx, rz)
            }
            2 -> {
                val rx = gridWidthExcl - gridX - widthExcl
                val rz = gridLengthExcl - gridZ - lengthExcl
                Translation(rx, rz)
            }
            3 -> {
                val rx = gridLengthExcl - gridZ - lengthExcl
                val rz = gridX
                Translation(rx, rz)
            }
            else -> Translation(gridX, gridZ)
        }
    }
}
