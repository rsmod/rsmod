package org.rsmod.game.region

import org.rsmod.map.zone.ZoneKey

@JvmInline
public value class RegionZoneKey(public val packed: Int) {
    public val rotation: Int
        get() = (packed shr ROTATION_BIT_OFFSET) and ROTATION_BIT_MASK

    public val normalX: Int
        get() = (packed shr X_BIT_OFFSET) and X_BIT_MASK

    public val normalZ: Int
        get() = (packed shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val normalLevel: Int
        get() = (packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK

    /**
     * Constructs the [ZoneKey] belonging to the `Normal` zone that this [RegionZoneKey] has copied.
     */
    public val normalZone: ZoneKey
        get() = ZoneKey(normalX, normalZ, normalLevel)

    public constructor(
        normalKey: ZoneKey,
        rotation: Int,
    ) : this(pack(rotation, normalKey.x, normalKey.z, normalKey.level))

    public companion object {
        public val NULL: RegionZoneKey = RegionZoneKey(-1)

        public const val UNKNOWN_FLAG_BIT_COUNT: Int = 1
        public const val ROTATION_BIT_COUNT: Int = 2
        public const val Z_BIT_COUNT: Int = 11
        public const val X_BIT_COUNT: Int = 10
        public const val LEVEL_BIT_COUNT: Int = 2

        public const val UNKNOWN_FLAG_BIT_OFFSET: Int = 0
        public const val ROTATION_BIT_OFFSET: Int = UNKNOWN_FLAG_BIT_OFFSET + UNKNOWN_FLAG_BIT_COUNT
        public const val Z_BIT_OFFSET: Int = ROTATION_BIT_OFFSET + ROTATION_BIT_COUNT
        public const val X_BIT_OFFSET: Int = Z_BIT_OFFSET + Z_BIT_COUNT
        public const val LEVEL_BIT_OFFSET: Int = X_BIT_OFFSET + X_BIT_COUNT

        public const val UNKNOWN_FLAG_BIT_MASK: Int = (1 shl UNKNOWN_FLAG_BIT_COUNT) - 1
        public const val ROTATION_BIT_MASK: Int = (1 shl ROTATION_BIT_COUNT) - 1
        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1
        public const val LEVEL_BIT_MASK: Int = (1 shl LEVEL_BIT_COUNT) - 1

        private fun pack(rotation: Int, x: Int, z: Int, level: Int): Int {
            require(rotation in 0..ROTATION_BIT_MASK) {
                "`rotation` value must be within range [0..$ROTATION_BIT_MASK]."
            }
            require(x in 0..X_BIT_MASK) { "`x` value must be within range [0..$X_BIT_MASK]." }
            require(z in 0..Z_BIT_MASK) { "`z` value must be within range [0..$Z_BIT_MASK]." }
            require(level in 0..LEVEL_BIT_MASK) {
                "`level` value must be within range [0..$LEVEL_BIT_MASK]."
            }
            return ((rotation and ROTATION_BIT_MASK) shl ROTATION_BIT_OFFSET) or
                ((x and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((z and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                ((level and LEVEL_BIT_MASK) shl LEVEL_BIT_OFFSET)
        }
    }
}
