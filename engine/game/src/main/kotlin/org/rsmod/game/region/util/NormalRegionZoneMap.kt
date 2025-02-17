package org.rsmod.game.region.util

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.rsmod.game.region.zone.RegionZoneCopy
import org.rsmod.game.region.zone.RegionZoneFlag
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey

public class NormalRegionZoneMap(public val backing: Int2IntOpenHashMap = Int2IntOpenHashMap()) {
    public operator fun get(normalCoords: CoordGrid, flag: RegionZoneFlag?): CoordGrid {
        val normalZone = ZoneKey.from(normalCoords)
        val regionCopy = get(normalZone, flag)

        if (regionCopy == RegionZone.NULL) {
            return CoordGrid.NULL
        }

        val normalBase = normalZone.toCoords()
        val normalDelta = normalCoords - normalBase

        val regionBase = regionCopy.zoneKey.toCoords()
        val regionCoords = regionBase + normalDelta

        if (regionCopy.rotation == 0) {
            return regionCoords
        }

        val normalGrid = ZoneGrid.from(normalCoords)
        val translation = RegionRotations.translateCoords(regionCopy.rotation, normalGrid)
        return regionBase.translate(translation)
    }

    private operator fun get(zone: ZoneKey, flag: RegionZoneFlag?): RegionZone {
        val normalZone = NormalZone.from(zone, flag)
        val packedRegionZone = backing[normalZone.packed]
        return if (packedRegionZone == backing.defaultReturnValue()) {
            RegionZone.NULL
        } else {
            RegionZone(packedRegionZone)
        }
    }

    public operator fun set(copyZone: RegionZoneCopy, regionZone: ZoneKey) {
        val regionCopyZone = RegionZone.from(regionZone, copyZone)
        val normalZone = NormalZone.from(copyZone)
        backing[normalZone.packed] = regionCopyZone.packed
    }

    @JvmInline
    public value class ReadOnly(private val backing: NormalRegionZoneMap) {
        public operator fun get(normalCoords: CoordGrid, flag: RegionZoneFlag? = null): CoordGrid {
            val regionCoords = backing[normalCoords, flag]
            check(regionCoords != CoordGrid.NULL) {
                val normalZone = ZoneKey.from(normalCoords)
                val invalidFlag = backing[normalCoords, null] != CoordGrid.NULL
                if (invalidFlag) {
                    "`normalCoords` are associated with the region, but it does not " +
                        "have `flag` set: flag=$flag, normalCoords=$normalCoords, " +
                        "normalZone=$normalZone"
                } else {
                    "`normalCoords` are not associated with a region zone: " +
                        "normalCoords=$normalCoords, normalZone=$normalZone"
                }
            }
            return regionCoords
        }

        public operator fun get(
            normalLevel: Int,
            normalMapSquareX: Int,
            normalMapSquareZ: Int,
            normalMapSquareGridX: Int,
            normalMapSquareGridZ: Int,
            flag: RegionZoneFlag? = null,
        ): CoordGrid {
            val mapSquare = MapSquareKey(normalMapSquareX, normalMapSquareZ)
            val mapSquareBase = mapSquare.toCoords(normalLevel)
            val normalCoords = mapSquareBase.translate(normalMapSquareGridX, normalMapSquareGridZ)
            return this[normalCoords, flag]
        }
    }

    @JvmInline
    private value class RegionZone(val packed: Int) {
        val zoneKey: ZoneKey
            get() = ZoneKey((packed shr ZONE_BIT_OFFSET) and ZONE_BIT_MASK)

        val rotation: Int
            get() = (packed shr ROTATION_BIT_OFFSET) and ROTATION_BIT_MASK

        companion object {
            val NULL: RegionZone = RegionZone(-1)

            const val ZONE_BIT_COUNT: Int = 24
            const val ROTATION_BIT_COUNT: Int = 2

            const val ZONE_BIT_OFFSET: Int = 0
            const val ROTATION_BIT_OFFSET: Int = ZONE_BIT_OFFSET + ZONE_BIT_COUNT

            const val ZONE_BIT_MASK: Int = (1 shl ZONE_BIT_COUNT) - 1
            const val ROTATION_BIT_MASK: Int = (1 shl ROTATION_BIT_COUNT) - 1

            fun from(regionZone: ZoneKey, copyZone: RegionZoneCopy): RegionZone {
                return RegionZone(pack(regionZone, copyZone.rotation))
            }

            fun pack(zone: ZoneKey, rotation: Int): Int {
                require(zone.packed in 0..ZONE_BIT_MASK) {
                    "`zone.packed` value cannot fit in zone bits."
                }
                require(rotation in 0..ROTATION_BIT_MASK) {
                    "`rotation` value must be within range [0..$ROTATION_BIT_MASK]."
                }
                return ((zone.packed and ZONE_BIT_MASK) shl ZONE_BIT_OFFSET) or
                    ((rotation and ROTATION_BIT_MASK) shl ROTATION_BIT_OFFSET)
            }
        }
    }

    @JvmInline
    private value class NormalZone private constructor(val packed: Int) {
        val zoneKey: ZoneKey
            get() = ZoneKey((packed shr ZONE_BIT_OFFSET) and ZONE_BIT_MASK)

        val uniqueFlag: Int
            get() = (packed shr UNIQUE_FLAG_BIT_OFFSET) and UNIQUE_FLAG_BIT_MASK

        companion object {
            init {
                assertProperUniqueFlagBits()
            }

            val NULL: RegionZone = RegionZone(-1)

            const val ZONE_BIT_COUNT: Int = 24
            const val UNIQUE_FLAG_BIT_COUNT: Int = 3

            const val ZONE_BIT_OFFSET: Int = 0
            const val UNIQUE_FLAG_BIT_OFFSET: Int = ZONE_BIT_OFFSET + ZONE_BIT_COUNT

            const val ZONE_BIT_MASK: Int = (1 shl ZONE_BIT_COUNT) - 1
            const val UNIQUE_FLAG_BIT_MASK: Int = (1 shl UNIQUE_FLAG_BIT_COUNT) - 1

            fun from(zone: ZoneKey, flag: RegionZoneFlag?): NormalZone {
                return NormalZone(pack(zone, flag?.bitmask ?: 0))
            }

            fun from(zone: RegionZoneCopy): NormalZone {
                return NormalZone(pack(zone.normalZone(), zone.uniqueFlag))
            }

            fun pack(zone: ZoneKey, flag: Int): Int {
                require(zone.packed in 0..ZONE_BIT_MASK) {
                    "`zone.packed` value cannot fit in zone bits."
                }
                require(flag in 0..UNIQUE_FLAG_BIT_MASK) {
                    "`flag` value must be within range [0..$UNIQUE_FLAG_BIT_MASK]."
                }
                return ((zone.packed and ZONE_BIT_MASK) shl ZONE_BIT_OFFSET) or
                    ((flag and UNIQUE_FLAG_BIT_MASK) shl UNIQUE_FLAG_BIT_OFFSET)
            }

            private fun assertProperUniqueFlagBits() {
                // We manually check this instead of simply assigning `UNIQUE_FLAG_BIT_COUNT` to
                // `RegionZoneCopy.UNIQUE_FLAG_BIT_COUNT` as a way to ensure that when/if this
                // value does change, the total bit count here does not go over the expected 32-bit
                // bounds. And if it does, we should change the packed value to a `Long` instead.
                check(UNIQUE_FLAG_BIT_COUNT == RegionZoneCopy.UNIQUE_FLAG_BIT_COUNT) {
                    "Unexpected `UNIQUE_FLAGS_BIT_COUNT` value: " +
                        "value=${UNIQUE_FLAG_BIT_COUNT}, " +
                        "expected=${RegionZoneCopy.UNIQUE_FLAG_BIT_COUNT}"
                }
            }
        }
    }
}
