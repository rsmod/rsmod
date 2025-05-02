package org.rsmod.map.square

import org.rsmod.map.CoordGrid

/**
 * [MapSquareGrid] represents a localized 64x64 grid within a single map square, using a compact
 * integer representation. This class is useful for operations within the limited coordinate space
 * of a map square.
 *
 * The grid is defined by three coordinates:
 * - `x`: The X coordinate, ranging from 0 to 63.
 * - `z`: The Z coordinate, ranging from 0 to 63.
 * - `level`: The level or height coordinate, ranging from 0 to 3.
 *
 * Each [MapSquareGrid] instance packs these coordinates into a single integer value [packed],
 * optimizing memory usage and simplifying coordinate calculations within the map square's 64x64
 * area.
 *
 * ### Example Usage:
 * Consider the absolute coordinates (3220, 3205, 0) within the game world. These can be converted
 * into a [MapSquareGrid] by calculating their local position within a 64x64 map square:
 * ```
 * - Absolute Coordinates: 3220, 3205, 0
 * - MapSquareGrid:          20,    5, 0
 * ```
 *
 * The limits of this grid are defined by constants [X_BIT_COUNT], [Z_BIT_COUNT], and
 * [LEVEL_BIT_COUNT], ensuring that [x], [z], and [level] are always within their respective bounds
 * (0-63 for x and z, 0-3 for level).
 *
 * This class provides several utility methods for translating grid positions and performing
 * arithmetic operations between grids.
 *
 * @property packed The integer representation of the grid coordinates and level.
 * @constructor Creates a [MapSquareGrid] instance using local [x], [z], and [level] coordinates. To
 *   convert from absolute coordinates, use [fromAbsolute] or [from].
 */
@JvmInline
public value class MapSquareGrid(public val packed: Int) {
    public val x: Int
        get() = (packed shr X_BIT_OFFSET) and X_BIT_MASK

    public val z: Int
        get() = (packed shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val level: Int
        get() = (packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK

    public constructor(x: Int, z: Int, level: Int = 0) : this(pack(x, z, level))

    public fun copy(x: Int = this.x, z: Int = this.z, level: Int = this.level): MapSquareGrid =
        MapSquareGrid(x, z, level)

    public fun translate(xOffset: Int, zOffset: Int, levelOffset: Int = 0): MapSquareGrid =
        MapSquareGrid(x = x + xOffset, z = z + zOffset, level = level + levelOffset)

    public fun translateX(offset: Int): MapSquareGrid = translate(offset, 0, 0)

    public fun translateZ(offset: Int): MapSquareGrid = translate(0, offset, 0)

    public fun translateLevel(offset: Int): MapSquareGrid = translate(0, 0, offset)

    public operator fun minus(other: MapSquareGrid): MapSquareGrid {
        return translate(-other.x, -other.z, -other.level)
    }

    public operator fun plus(other: MapSquareGrid): MapSquareGrid {
        return translate(other.x, other.z, other.level)
    }

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public operator fun component3(): Int = level

    public override fun toString(): String {
        return "MapSquareGrid(x=$x, y=$z, level=$level)"
    }

    public companion object {
        public val ZERO: MapSquareGrid = MapSquareGrid(0)
        public val NULL: MapSquareGrid = MapSquareGrid(-1)

        /**
         * This value represents the length of one side of a map square. Therefore, a map square
         * covers a total area of 64x64 tiles in the x and z directions.
         */
        public const val LENGTH: Int = 64

        public const val Z_BIT_COUNT: Int = 6
        public const val X_BIT_COUNT: Int = 6
        public const val LEVEL_BIT_COUNT: Int = 2

        public const val Z_BIT_OFFSET: Int = 0
        public const val X_BIT_OFFSET: Int = Z_BIT_OFFSET + Z_BIT_COUNT
        public const val LEVEL_BIT_OFFSET: Int = X_BIT_OFFSET + X_BIT_COUNT

        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1
        public const val LEVEL_BIT_MASK: Int = (1 shl LEVEL_BIT_COUNT) - 1

        /**
         * Converts the absolute coordinates ([x], [z], [level]) to [MapSquareGrid].
         *
         * In other words, this function will trim down the given coordinates in order to fit the
         * respective amount of bits in [MapSquareGrid].
         */
        public fun fromAbsolute(x: Int, z: Int, level: Int): MapSquareGrid =
            MapSquareGrid(
                x = x and X_BIT_MASK,
                z = z and Z_BIT_MASK,
                level = level and LEVEL_BIT_MASK,
            )

        public fun from(coords: CoordGrid): MapSquareGrid =
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
