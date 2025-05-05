package org.rsmod.map.zone

import org.rsmod.map.CoordGrid

/**
 * [ZoneGrid] represents a localized 8x8 grid within a single zone, using a compact integer
 * representation. This class facilitates operations within the limited coordinate space of a zone.
 *
 * The grid is defined by three coordinates:
 * - `x`: The X coordinate, ranging from 0 to 7.
 * - `z`: The Z coordinate, ranging from 0 to 7.
 * - `level`: The level or height coordinate, ranging from 0 to 3.
 *
 * Each [ZoneGrid] instance packs these coordinates into a single integer value [packed], optimizing
 * memory usage and simplifying coordinate calculations within the zone's 8x8 area.
 *
 * ### Example Usage:
 * Suppose you have absolute coordinates (3220, 3205, 0) in the game world. These coordinates can be
 * converted into a [ZoneGrid] by determining their local position within an 8x8 zone:
 * ```
 * - Absolute Coordinates: 3220, 3205, 0
 * - ZoneGrid:                4,    5, 0
 * ```
 *
 * The limits of this grid are defined by constants [X_BIT_COUNT], [Z_BIT_COUNT], and
 * [LEVEL_BIT_COUNT], ensuring that [x], [z], and [level] are always within their respective bounds
 * (0-7 for x and z, 0-3 for level).
 *
 * This class provides utility methods for translating grid positions and performing arithmetic
 * operations between grids.
 *
 * @property packed The integer representation of the grid coordinates and level.
 * @constructor Creates a [ZoneGrid] instance using local [x], [z], and [level] coordinates. To
 *   convert from absolute coordinates, use [fromAbsolute] or [from].
 */
@JvmInline
public value class ZoneGrid(public val packed: Int) {
    public val x: Int
        get() = (packed shr X_BIT_OFFSET) and X_BIT_MASK

    public val z: Int
        get() = (packed shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val level: Int
        get() = (packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK

    public constructor(x: Int, z: Int, level: Int = 0) : this(pack(x, z, level))

    public fun translate(xOffset: Int, zOffset: Int, levelOffset: Int = 0): ZoneGrid =
        ZoneGrid(x = x + xOffset, z = z + zOffset, level = level + levelOffset)

    public fun translateX(offset: Int): ZoneGrid = translate(offset, 0, 0)

    public fun translateZ(offset: Int): ZoneGrid = translate(0, offset, 0)

    public fun translateLevel(offset: Int): ZoneGrid = translate(0, 0, offset)

    public operator fun minus(other: ZoneGrid): ZoneGrid {
        return translate(-other.x, -other.z, -other.level)
    }

    public operator fun plus(other: ZoneGrid): ZoneGrid {
        return translate(other.x, other.z, other.level)
    }

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public operator fun component3(): Int = level

    public override fun toString(): String = "ZoneGrid(x=$x, y=$z, level=$level)"

    public companion object {
        public val ZERO: ZoneGrid = ZoneGrid(0)

        /**
         * This value represents the number of tiles per side of a zone. Therefore, a zone covers an
         * `8 x 8` tile area in the x and z directions.
         */
        public const val LENGTH: Int = 8

        public const val Z_BIT_COUNT: Int = 3
        public const val X_BIT_COUNT: Int = 3
        public const val LEVEL_BIT_COUNT: Int = 2

        public const val Z_BIT_OFFSET: Int = 0
        public const val X_BIT_OFFSET: Int = Z_BIT_OFFSET + Z_BIT_COUNT
        public const val LEVEL_BIT_OFFSET: Int = X_BIT_OFFSET + X_BIT_COUNT

        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1
        public const val LEVEL_BIT_MASK: Int = (1 shl LEVEL_BIT_COUNT) - 1

        /**
         * Converts the absolute coordinates ([x], [z], [level]) to [ZoneGrid].
         *
         * In other words, this function will trim down the given coordinates in order to fit the
         * respective amount of bits in [ZoneGrid].
         */
        public fun fromAbsolute(x: Int, z: Int, level: Int): ZoneGrid =
            ZoneGrid(x = x and X_BIT_MASK, z = z and Z_BIT_MASK, level = level and LEVEL_BIT_MASK)

        public fun from(coords: CoordGrid): ZoneGrid =
            fromAbsolute(x = coords.x, z = coords.z, level = coords.level)

        private fun pack(x: Int, z: Int, level: Int): Int {
            require(x in 0..X_BIT_MASK) { "`x` value must be within range [0..$X_BIT_MASK]." }
            require(z in 0..Z_BIT_MASK) { "`z` value must be within range [0..$Z_BIT_MASK]." }
            require(level in 0..LEVEL_BIT_MASK) {
                "`level` value must be within range [0..$LEVEL_BIT_MASK]."
            }
            return ((x and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((z and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                ((level and LEVEL_BIT_MASK) shl LEVEL_BIT_OFFSET)
        }
    }
}
