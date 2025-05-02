package org.rsmod.map.util

/**
 * [LocalMapSquareZone] represents a local 8x8 zone index within a single 64x64 map square.
 *
 * The index is packed into an integer using three components:
 * - `x`: The zone X index in the map square, ranging from 0 to 7.
 * - `z`: The zone Z index in the map square, ranging from 0 to 7.
 * - `level`: The level or height coordinate, ranging from 0 to 3.
 *
 * This class is **not** semantically equivalent to `ZoneGrid`, which represents a coordinate within
 * a single 8x8 zone. In contrast, [LocalMapSquareZone] refers to which zone is being addressed
 * within a map square.
 *
 * @property packed The packed integer representation of the zone index within a 64x64 map square.
 */
@JvmInline
public value class LocalMapSquareZone(public val packed: Int) {
    public val x: Int
        get() = (packed shr X_BIT_OFFSET) and X_BIT_MASK

    public val z: Int
        get() = (packed shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val level: Int
        get() = (packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK

    public constructor(x: Int, z: Int, level: Int) : this(pack(x, z, level))

    public override fun toString(): String = "LocalMapSquareZone(x=$x, z=$z, level=$level)"

    public companion object {
        /**
         * This value represents the number of zones per side of a map square. Therefore, a map
         * square covers an `8 x 8` zone area in the x and z directions.
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

        private fun pack(x: Int, z: Int, level: Int): Int {
            require(x in 0..X_BIT_MASK) { "`x` value must be within range [0..$X_BIT_MASK]." }
            require(z in 0..Z_BIT_MASK) { "`z` value must be within range [0..$Z_BIT_MASK]." }
            require(level in 0..LEVEL_BIT_MASK) {
                "`level` value must be within range [0..$LEVEL_BIT_MASK]."
            }
            return (x shl X_BIT_OFFSET) or (z shl Z_BIT_OFFSET) or (level shl LEVEL_BIT_OFFSET)
        }
    }
}
