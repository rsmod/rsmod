package org.rsmod.game.region.zone

import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

@DslMarker private annotation class RegionZoneBuilderDsl

@RegionZoneBuilderDsl
public class RegionZoneBuilder(private val regionZoneLength: Int) {
    private val backing: RegionZoneCopyMap = RegionZoneCopyMap()

    public fun build(southWest: ZoneKey): RegionZoneCopyMap = backing.translate(southWest)

    public operator fun set(
        regionZoneX: Int,
        regionZoneZ: Int,
        regionLevel: Int,
        normalZone: ZoneKey,
    ) {
        val copy = RegionZoneCopy(normalZone, rotation = 0)
        this[regionZoneX, regionZoneZ, regionLevel] = copy
    }

    public operator fun set(
        regionZoneX: Int,
        regionZoneZ: Int,
        regionLevel: Int,
        normalZoneCopy: RegionZoneCopy,
    ) {
        assertZone(regionZoneX, regionZoneZ, regionLevel)
        val regionZone = ZoneKey(regionZoneX, regionZoneZ, regionLevel)
        backing[regionZone] = normalZoneCopy
    }

    public fun ZoneKey.rotate(rotation: Int): RegionZoneCopy {
        require(rotation in 0..3) { "Rotation must be within range [0..3]. (rotation=$rotation)" }
        return RegionZoneCopy(this, rotation)
    }

    public fun ZoneKey.rotate90(): RegionZoneCopy = rotate(rotation = 1)

    public fun ZoneKey.rotate180(): RegionZoneCopy = rotate(rotation = 2)

    public fun ZoneKey.rotate270(): RegionZoneCopy = rotate(rotation = 3)

    private fun assertZone(zoneX: Int, zoneZ: Int, level: Int) {
        require(zoneX in 0..<regionZoneLength) {
            "`zoneX` should be within range [0..${regionZoneLength - 1}]. (zoneX=$zoneX)"
        }

        require(zoneZ in 0..<regionZoneLength) {
            "`zoneZ` should be within range [0..${regionZoneLength - 1}]. (zoneZ=$zoneZ)"
        }

        require(level in 0..CoordGrid.LEVEL_BIT_MASK) {
            "`level` should be within range [0..${CoordGrid.LEVEL_BIT_MASK}]. (level=$level)"
        }
    }
}
