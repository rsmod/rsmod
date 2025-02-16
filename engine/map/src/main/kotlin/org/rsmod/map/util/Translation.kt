package org.rsmod.map.util

import kotlin.math.abs

/**
 * Represents a translation vector that can be applied to a [org.rsmod.map.CoordGrid] for operations
 * such as shifting its position. This value class packs the `x`, `z`, and `level` translation
 * values into a single `Int` to optimize memory usage and performance. The coordinate values are
 * capped to 14 bits, while the level is capped to 3 bits.
 *
 * Since this class supports negative values, the maximum range for `x` and `z` is from `-8192` to
 * `8191`, and for `level`, it is from `-4` to `3`. These values allow for both positive and
 * negative translations.
 *
 * **Main purpose**: This class is designed to be used for applying translations to
 * [org.rsmod.map.CoordGrid] instances, allowing operations such as addition and subtraction of grid
 * coordinates. However, it can be used in other contexts where a translation vector is needed.
 *
 * @property packed The packed integer containing the `x`, `z`, and `level` translation values.
 */
@JvmInline
public value class Translation(public val packed: Int) {
    public val x: Int
        get() = (packed shl (32 - X_BIT_COUNT - X_BIT_OFFSET)) shr (32 - X_BIT_COUNT)

    public val z: Int
        get() = (packed shl (32 - Z_BIT_COUNT - Z_BIT_OFFSET)) shr (32 - Z_BIT_COUNT)

    public val level: Int
        get() = (packed shl (32 - LEVEL_BIT_COUNT - LEVEL_BIT_OFFSET)) shr (32 - LEVEL_BIT_COUNT)

    public constructor(x: Int = 0, z: Int = 0, level: Int = 0) : this(pack(x, z, level))

    public fun copy(x: Int = this.x, z: Int = this.z, level: Int = this.level): Translation =
        Translation(x, z, level)

    public fun absolute(): Translation = Translation(abs(x), abs(z), abs(level))

    public operator fun plus(other: Translation): Translation =
        Translation(x = x + other.x, z = z + other.z, level = level + other.level)

    public operator fun minus(other: Translation): Translation =
        Translation(x = x - other.x, z = z - other.z, level = level - other.level)

    public operator fun times(other: Translation): Translation =
        Translation(x = x * other.x, z = z * other.z, level = level * other.level)

    override fun toString(): String = "Translation(x=$x, y=$z, level=$level)"

    public companion object {
        public val ZERO: Translation = Translation(0)

        public const val Z_BIT_COUNT: Int = 14
        public const val X_BIT_COUNT: Int = 14
        public const val LEVEL_BIT_COUNT: Int = 3

        public const val Z_BIT_OFFSET: Int = 0
        public const val X_BIT_OFFSET: Int = Z_BIT_OFFSET + Z_BIT_COUNT
        public const val LEVEL_BIT_OFFSET: Int = X_BIT_OFFSET + X_BIT_COUNT

        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1
        public const val LEVEL_BIT_MASK: Int = (1 shl LEVEL_BIT_COUNT) - 1

        private const val MAX_X: Int = (1 shl (X_BIT_COUNT - 1)) - 1
        private const val MIN_X: Int = -(1 shl (X_BIT_COUNT - 1))
        private const val MAX_Z: Int = (1 shl (Z_BIT_COUNT - 1)) - 1
        private const val MIN_Z: Int = -(1 shl (Z_BIT_COUNT - 1))
        private const val MAX_LEVEL: Int = (1 shl (LEVEL_BIT_COUNT - 1)) - 1
        private const val MIN_LEVEL: Int = -(1 shl (LEVEL_BIT_COUNT - 1))

        private fun pack(x: Int, z: Int, level: Int): Int {
            require(x in MIN_X..MAX_X) { "`x` value must be within range [$MIN_X..$MAX_X]. (x=$x)" }
            require(z in MIN_Z..MAX_Z) { "`z` value must be within range [$MIN_Z..$MAX_Z]. (z=$z)" }
            require(level in MIN_LEVEL..MAX_LEVEL) {
                "`level` value must be within range [$MIN_LEVEL..$MAX_LEVEL]. (level=$level)"
            }
            return ((x and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((z and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                ((level and LEVEL_BIT_MASK) shl LEVEL_BIT_OFFSET)
        }
    }
}
