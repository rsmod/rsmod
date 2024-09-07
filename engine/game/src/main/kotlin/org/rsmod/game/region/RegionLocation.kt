package org.rsmod.game.region

import org.rsmod.game.loc.LocEntity
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey

@JvmInline
public value class RegionLocation(public val coords: CoordGrid) {
    public fun southWestZone(): ZoneKey = ZoneKey.from(coords)

    public fun southEastZone(adjustedWidth: Int): ZoneKey =
        ZoneKey.from(coords.translateX(adjustedWidth - 1))

    public fun northWestZone(adjustedLength: Int): ZoneKey =
        ZoneKey.from(coords.translateZ(adjustedLength - 1))

    public fun northEastZone(adjustedWidth: Int, adjustedLength: Int): ZoneKey =
        ZoneKey.from(coords.translate(adjustedWidth - 1, adjustedLength - 1))

    public fun rotate(
        locAdjustedAngle: Int,
        locAdjustedWidth: Int,
        locAdjustedLength: Int,
    ): CoordGrid {
        require(locAdjustedAngle in 0..LocEntity.ANGLE_BIT_MASK)
        val locWidthExcl = locAdjustedWidth - 1
        val locLengthExcl = locAdjustedLength - 1
        val zoneGridX = coords.x % ZoneGrid.LENGTH
        val zoneGridZ = coords.z % ZoneGrid.LENGTH
        val baseZoneGrid = ZoneKey.from(coords).toCoords()
        return when (locAdjustedAngle) {
            1 -> {
                val rx = zoneGridZ
                val rz = ZONE_LENGTH_EXCLUSIVE - zoneGridX - locWidthExcl
                baseZoneGrid.translate(rx, rz)
            }
            2 -> {
                val rx = ZONE_LENGTH_EXCLUSIVE - zoneGridX - locWidthExcl
                val rz = ZONE_LENGTH_EXCLUSIVE - zoneGridZ - locLengthExcl
                baseZoneGrid.translate(rx, rz)
            }
            3 -> {
                val rx = ZONE_LENGTH_EXCLUSIVE - zoneGridZ - locLengthExcl
                val rz = zoneGridX
                baseZoneGrid.translate(rx, rz)
            }
            else -> coords
        }
    }

    private companion object {
        private const val ZONE_LENGTH_EXCLUSIVE: Int = ZoneGrid.LENGTH - 1
    }
}
