package org.rsmod.plugins.info.player.model.coord

@JvmInline
public value class HighResCoord(public val packed: Int) {

    public val x: Int get() = (packed shr X_BIT_OFFSET) and X_BIT_MASK

    public val z: Int get() = (packed shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val level: Int get() = (packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK

    public constructor(x: Int, z: Int, level: Int = 0) : this(pack(x, z, level))

    public fun toLowRes(): LowResCoord {
        return LowResCoord((x shr 13) and 0xFF, z shr 13, level)
    }

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public operator fun component3(): Int = level

    public operator fun minus(other: HighResCoord): HighResCoord {
        val x = (x - other.x) and X_BIT_MASK
        val z = (z - other.z) and Z_BIT_MASK
        val level = (level - other.level) and LEVEL_BIT_MASK
        return HighResCoord(x, z, level)
    }

    public operator fun plus(other: HighResCoord): HighResCoord {
        val x = (x + other.x) and X_BIT_MASK
        val z = (z + other.z) and Z_BIT_MASK
        val level = (level + other.level) and LEVEL_BIT_MASK
        return HighResCoord(x, z, level)
    }

    public override fun toString(): String {
        return "HighResCoord(x=$x, z=$z, level=$level)"
    }

    public companion object {

        public val ZERO: HighResCoord = HighResCoord(0)

        public const val X_BIT_COUNT: Int = 14
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1

        public const val Z_BIT_COUNT: Int = 14
        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1

        public const val LEVEL_BIT_COUNT: Int = 2
        public const val LEVEL_BIT_MASK: Int = (1 shl LEVEL_BIT_COUNT) - 1
        public const val LEVEL_COUNT: Int = LEVEL_BIT_MASK + 1

        public const val Z_BIT_OFFSET: Int = 0
        public const val X_BIT_OFFSET: Int = Z_BIT_COUNT
        public const val LEVEL_BIT_OFFSET: Int = Z_BIT_COUNT + X_BIT_COUNT

        private fun pack(x: Int, z: Int, level: Int): Int {
            if (x !in 0..X_BIT_MASK) {
                throw IllegalArgumentException("`x` value must be within range [0..$X_BIT_MASK].")
            } else if (z !in 0..Z_BIT_MASK) {
                throw IllegalArgumentException("`z` value must be within range [0..$Z_BIT_MASK].")
            } else if (level !in 0..LEVEL_BIT_MASK) {
                throw IllegalArgumentException("`level` value must be within range [0..$LEVEL_BIT_MASK].")
            }
            return ((x and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((z and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                ((level and LEVEL_BIT_MASK) shl LEVEL_BIT_OFFSET)
        }
    }
}
