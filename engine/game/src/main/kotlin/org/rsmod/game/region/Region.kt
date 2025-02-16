package org.rsmod.game.region

import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey

public data class Region(
    public val southWest: CoordGrid,
    public val northEast: CoordGrid,
    public val uid: Int,
    public val slot: Int,
) {
    public val zoneLength: Int
        get() = (northEast.x - southWest.x) / ZoneGrid.LENGTH

    public fun toZoneList(): List<ZoneKey> = buildList {
        addZones(level = 0)
        addZones(level = 1)
        addZones(level = 2)
        addZones(level = 3)
    }

    public fun toZoneList(level: Int): List<ZoneKey> = buildList { addZones(level) }

    private fun MutableList<ZoneKey>.addZones(level: Int) {
        val length = zoneLength
        for (zoneX in 0 until length) {
            for (zoneZ in 0 until length) {
                val coordX = southWest.x + (zoneX * ZoneGrid.LENGTH)
                val coordZ = southWest.z + (zoneZ * ZoneGrid.LENGTH)
                this += ZoneKey.fromAbsolute(coordX, coordZ, level)
            }
        }
    }

    override fun toString(): String =
        "Region(" +
            "slot=$slot, " +
            "uid=$uid, " +
            "zoneCount=($zoneLength x $zoneLength), " +
            "southWest=$southWest, " +
            "northEast=$northEast" +
            ")"

    public companion object {
        public const val INVALID_SLOT: Int = -1
    }
}
