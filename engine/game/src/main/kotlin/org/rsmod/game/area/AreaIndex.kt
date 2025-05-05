package org.rsmod.game.area

import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap
import it.unimi.dsi.fastutil.shorts.ShortArrayList
import org.rsmod.annotations.InternalApi
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.util.FastPack
import org.rsmod.map.zone.ZoneKey

public class AreaIndex {
    /*
     * Note: For optimization, areas can be registered against coarser granularity (zones and map
     * squares). This reduces memory usage and startup time but means a single coordinate can be
     * matched by multiple overlapping entries. Each category (coord, zone, map square) supports up
     * to 5 areas, so in total a coord may resolve to up to 15 areas.
     *
     * This index delegates emulation accuracy; it is up to the registry to ensure a coordinate does
     * not resolve to more than 5 areas.
     */
    private val coordAreas = PackedAreaMap()
    private val zoneAreas = PackedAreaMap()
    private val mapSquareAreas = PackedAreaMap()

    /**
     * Appends all areas found at the given coordinate to [dest].
     *
     * _Note: [dest] must be cleared by caller when appropriate._
     */
    public fun putAreas(coord: CoordGrid, dest: ShortArrayList) {
        coordAreas.get(coord.packed, dest)

        val zone = FastPack.zoneKey(coord)
        zoneAreas.get(zone, dest)

        val mapSquare = FastPack.mapSquareKey(coord)
        mapSquareAreas.get(mapSquare, dest)
    }

    @InternalApi
    public fun registerAll(coord: CoordGrid, areas: Iterator<Short>) {
        val area1 = areas.nextPlusOneOrZero()
        val area2 = areas.nextPlusOneOrZero()
        val area3 = areas.nextPlusOneOrZero()
        val area4 = areas.nextPlusOneOrZero()
        val area5 = areas.nextPlusOneOrZero()
        check(!areas.hasNext()) { "Can only register up to $MAX_AREAS_PER_KEY areas: $coord" }
        coordAreas.register(coord.packed, area1, area2, area3, area4, area5)
    }

    @InternalApi
    public fun registerAll(zone: ZoneKey, areas: Iterator<Short>) {
        val area1 = areas.nextPlusOneOrZero()
        val area2 = areas.nextPlusOneOrZero()
        val area3 = areas.nextPlusOneOrZero()
        val area4 = areas.nextPlusOneOrZero()
        val area5 = areas.nextPlusOneOrZero()
        check(!areas.hasNext()) { "Can only register up to $MAX_AREAS_PER_KEY areas: $zone" }
        zoneAreas.register(zone.packed, area1, area2, area3, area4, area5)
    }

    @InternalApi
    public fun registerAll(mapSquare: MapSquareKey, areas: Iterator<Short>) {
        val area1 = areas.nextPlusOneOrZero()
        val area2 = areas.nextPlusOneOrZero()
        val area3 = areas.nextPlusOneOrZero()
        val area4 = areas.nextPlusOneOrZero()
        val area5 = areas.nextPlusOneOrZero()
        check(!areas.hasNext()) { "Can only register up to $MAX_AREAS_PER_KEY areas: $mapSquare" }
        mapSquareAreas.register(mapSquare.id, area1, area2, area3, area4, area5)
    }

    // Area ids are stored +1 internally so that `0` can be used as a valid id.
    private fun Iterator<Short>.nextPlusOneOrZero(): Short {
        if (!hasNext()) {
            return 0
        }
        val next = next()
        check(next in 0..<32767) { "Area id must be in range [0..32766]. ($next)" }
        return (next + 1).toShort()
    }

    private class PackedAreaMap {
        private val areas = Int2LongOpenHashMap()
        private val overflow = Int2ShortOpenHashMap()

        fun get(key: Int, dest: ShortArrayList) {
            val packed = areas[key]
            if (packed == areas.defaultReturnValue()) {
                return
            }
            unpackInto(packed, dest)
            if (hasOverflow(packed)) {
                val overflowArea = overflow[key]
                dest.add((overflowArea - 1).toShort())
            }
        }

        fun register(
            key: Int,
            area1: Short,
            area2: Short,
            area3: Short,
            area4: Short,
            area5: Short,
        ) {
            require(key !in areas) { "Key already registered: $key" }
            require(area1 != SHORT_ZERO) { "At least one area must be non-zero." }

            val hasOverflow = area5 != SHORT_ZERO
            areas[key] = pack(area1, area2, area3, area4, hasOverflow)
            if (hasOverflow) {
                overflow[key] = area5
            }
        }

        private companion object {
            private const val AREA_BIT_COUNT = 15
            private const val AREA_BIT_MASK = (1L shl AREA_BIT_COUNT) - 1

            private const val SLOT_1_OFFSET = 0
            private const val SLOT_2_OFFSET = SLOT_1_OFFSET + AREA_BIT_COUNT
            private const val SLOT_3_OFFSET = SLOT_2_OFFSET + AREA_BIT_COUNT
            private const val SLOT_4_OFFSET = SLOT_3_OFFSET + AREA_BIT_COUNT
            private const val OVERFLOW_FLAG_OFFSET = SLOT_4_OFFSET + 1

            private const val OVERFLOW_BIT_FLAG = 1L shl OVERFLOW_FLAG_OFFSET

            private fun hasOverflow(packed: Long): Boolean = (packed and OVERFLOW_BIT_FLAG) != 0L

            private fun unpackInto(packed: Long, dest: ShortArrayList) {
                val area1 = ((packed shr SLOT_1_OFFSET) and AREA_BIT_MASK).toShort()
                if (area1 != SHORT_ZERO) {
                    dest.add((area1 - 1).toShort())
                }

                val area2 = ((packed shr SLOT_2_OFFSET) and AREA_BIT_MASK).toShort()
                if (area2 != SHORT_ZERO) {
                    dest.add((area2 - 1).toShort())
                }

                val area3 = ((packed shr SLOT_3_OFFSET) and AREA_BIT_MASK).toShort()
                if (area3 != SHORT_ZERO) {
                    dest.add((area3 - 1).toShort())
                }

                val area4 = ((packed shr SLOT_4_OFFSET) and AREA_BIT_MASK).toShort()
                if (area4 != SHORT_ZERO) {
                    dest.add((area4 - 1).toShort())
                }
            }

            private fun pack(
                area1: Short,
                area2: Short,
                area3: Short,
                area4: Short,
                hasOverflow: Boolean,
            ): Long {
                var result = 0L
                result = result or ((area1.toLong() and AREA_BIT_MASK) shl SLOT_1_OFFSET)
                result = result or ((area2.toLong() and AREA_BIT_MASK) shl SLOT_2_OFFSET)
                result = result or ((area3.toLong() and AREA_BIT_MASK) shl SLOT_3_OFFSET)
                result = result or ((area4.toLong() and AREA_BIT_MASK) shl SLOT_4_OFFSET)
                if (hasOverflow) {
                    result = result or OVERFLOW_BIT_FLAG
                }
                return result
            }
        }
    }

    public companion object {
        public const val MAX_AREAS_PER_KEY: Int = 5
        private const val SHORT_ZERO: Short = 0
    }
}
