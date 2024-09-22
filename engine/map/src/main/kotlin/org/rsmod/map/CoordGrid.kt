package org.rsmod.map

import kotlin.math.abs
import kotlin.math.max
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.util.Translation

@JvmInline
public value class CoordGrid(public val packed: Int) {
    public val x: Int
        get() = (packed shr X_BIT_OFFSET) and X_BIT_MASK

    public val z: Int
        get() = (packed shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val level: Int
        get() = (packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK

    public val mx: Int
        get() = x / MapSquareGrid.LENGTH

    public val mz: Int
        get() = z / MapSquareGrid.LENGTH

    public val lx: Int
        get() = x % MapSquareGrid.LENGTH

    public val lz: Int
        get() = z % MapSquareGrid.LENGTH

    public constructor(x: Int, z: Int, level: Int = 0) : this(pack(x, z, level))

    public constructor(
        level: Int,
        mx: Int,
        mz: Int,
        lx: Int,
        lz: Int,
    ) : this(pack(level, mx, mz, lx, lz))

    public fun copy(x: Int = this.x, z: Int = this.z, level: Int = this.level): CoordGrid =
        CoordGrid(x, z, level)

    public fun translate(xOffset: Int, zOffset: Int, levelOffset: Int = 0): CoordGrid =
        CoordGrid(x = x + xOffset, z = z + zOffset, level = level + levelOffset)

    public fun translateX(offset: Int): CoordGrid = translate(offset, 0, 0)

    public fun translateZ(offset: Int): CoordGrid = translate(0, offset, 0)

    public fun translateLevel(offset: Int): CoordGrid = translate(0, 0, offset)

    public fun translate(translation: Translation): CoordGrid =
        translate(translation.x, translation.z, translation.level)

    /**
     * Chebyshev distance between two [org.rsmod.map.CoordGrid]s is used for specific scenarios. For
     * general distance calculation consider using [org.rsmod.map.util.Bounds] instead, which takes
     * width and height dimensions into account.
     */
    public fun chebyshevDistance(other: CoordGrid): Int = max(abs(x - other.x), abs(z - other.z))

    public operator fun minus(other: CoordGrid): CoordGrid {
        return translate(-other.x, -other.z, -other.level)
    }

    public operator fun plus(other: CoordGrid): CoordGrid {
        return translate(other.x, other.z, other.level)
    }

    public operator fun minus(translation: Translation): CoordGrid {
        return translate(-translation.x, -translation.z, -translation.level)
    }

    public operator fun plus(translation: Translation): CoordGrid {
        return translate(translation.x, translation.z, translation.level)
    }

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public operator fun component3(): Int = level

    public override fun toString(): String = "CoordGrid(${level}_${mx}_${mz}_${lx}_${lz})"

    public fun toConventionalString(): String = "CoordGrid(x=$x, z=$z, level=$level)"

    public companion object {
        public val ZERO: CoordGrid = CoordGrid(0)
        public val NULL: CoordGrid = CoordGrid(-1)

        public const val Z_BIT_COUNT: Int = 14
        public const val X_BIT_COUNT: Int = 14
        public const val LEVEL_BIT_COUNT: Int = 2

        public const val Z_BIT_OFFSET: Int = 0
        public const val X_BIT_OFFSET: Int = Z_BIT_OFFSET + Z_BIT_COUNT
        public const val LEVEL_BIT_OFFSET: Int = X_BIT_OFFSET + X_BIT_COUNT

        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1
        public const val LEVEL_BIT_MASK: Int = (1 shl LEVEL_BIT_COUNT) - 1

        /**
         * The maximum X coordinate value that can be represented within the game world.
         *
         * This constant defines the width of the game map in terms of coordinate units. It is
         * calculated based on the bit count used for X coordinates, allowing for a range from `0`
         * up to, but not including, `16384`. Thus, the maximum representable X coordinate is
         * `16383`.
         */
        public const val MAP_WIDTH: Int = X_BIT_MASK + 1

        /**
         * The maximum Z coordinate value that can be represented within the game world.
         *
         * This constant defines the length of the game map in terms of coordinate units. It is
         * calculated based on the bit count used for Z coordinates, allowing for a range from `0`
         * up to, but not including, `16384`. Therefore, the maximum representable Z coordinate is
         * `16383`.
         */
        public const val MAP_LENGTH: Int = Z_BIT_MASK + 1

        /**
         * The maximum number of levels (or height) that can be represented within the game world.
         *
         * This constant defines the number of discrete levels available in the game, calculated
         * based on the bit mask for levels. It allows for a range from 0 up to, but not including,
         * `4`, making the maximum representable level `3`.
         */
        public const val LEVEL_COUNT: Int = LEVEL_BIT_MASK + 1

        private fun pack(x: Int, z: Int, level: Int): Int {
            require(x in 0..X_BIT_MASK) {
                "`x` value must be within range [0..$X_BIT_MASK]. (x=$x)"
            }
            require(z in 0..Z_BIT_MASK) {
                "`z` value must be within range [0..$Z_BIT_MASK]. (z=$z)"
            }
            require(level in 0..LEVEL_BIT_MASK) {
                "`level` value must be within range [0..$LEVEL_BIT_MASK]. (level=$level)"
            }
            return ((x and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((z and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                ((level and LEVEL_BIT_MASK) shl LEVEL_BIT_OFFSET)
        }

        private fun pack(level: Int, mx: Int, mz: Int, lx: Int, lz: Int): Int {
            val key = MapSquareKey(mx, mz)
            val grid = MapSquareGrid(lx, lz)
            return pack(
                x = key.x * MapSquareGrid.LENGTH + grid.x,
                z = key.z * MapSquareGrid.LENGTH + grid.z,
                level = level,
            )
        }
    }
}
