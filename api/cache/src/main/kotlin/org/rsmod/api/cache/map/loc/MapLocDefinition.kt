package org.rsmod.api.cache.map.loc

@JvmInline
public value class MapLocDefinition(public val packed: Long) {
    public val id: Int
        get() = ((packed shr ID_BIT_OFFSET) and ID_BIT_MASK).toInt()

    public val shape: Int
        get() = ((packed shr SHAPE_BIT_OFFSET) and SHAPE_BIT_MASK).toInt()

    public val angle: Int
        get() = ((packed shr ANGLE_BIT_OFFSET) and ANGLE_BIT_MASK).toInt()

    public val localX: Int
        get() = ((packed shr X_BIT_OFFSET) and X_BIT_MASK).toInt()

    public val localZ: Int
        get() = ((packed shr Z_BIT_OFFSET) and Z_BIT_MASK).toInt()

    public val level: Int
        get() = ((packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK).toInt()

    public constructor(
        id: Int,
        localCoords: Int,
        attributes: Int,
    ) : this(pack(id, localCoords, attributes))

    public fun packedCoord(): Int = (level shl 12) or (localX shl 6) or localZ

    public fun packedAttributes(): Int = (shape shl 2) or angle

    public companion object {
        public const val ID_BIT_COUNT: Int = 17
        public const val SHAPE_BIT_COUNT: Int = 5
        public const val ANGLE_BIT_COUNT: Int = 2
        public const val Z_BIT_COUNT: Int = 6
        public const val X_BIT_COUNT: Int = 6
        public const val LEVEL_BIT_COUNT: Int = 2

        public const val ID_BIT_OFFSET: Int = 0
        public const val SHAPE_BIT_OFFSET: Int = ID_BIT_OFFSET + ID_BIT_COUNT
        public const val ANGLE_BIT_OFFSET: Int = SHAPE_BIT_OFFSET + SHAPE_BIT_COUNT
        public const val Z_BIT_OFFSET: Int = ANGLE_BIT_OFFSET + ANGLE_BIT_COUNT
        public const val X_BIT_OFFSET: Int = Z_BIT_OFFSET + Z_BIT_COUNT
        public const val LEVEL_BIT_OFFSET: Int = X_BIT_OFFSET + X_BIT_COUNT

        public const val ID_BIT_MASK: Long = (1L shl ID_BIT_COUNT) - 1
        public const val SHAPE_BIT_MASK: Long = (1L shl SHAPE_BIT_COUNT) - 1
        public const val ANGLE_BIT_MASK: Long = (1L shl ANGLE_BIT_COUNT) - 1
        public const val Z_BIT_MASK: Long = (1L shl Z_BIT_COUNT) - 1
        public const val X_BIT_MASK: Long = (1L shl X_BIT_COUNT) - 1
        public const val LEVEL_BIT_MASK: Long = (1L shl LEVEL_BIT_COUNT) - 1

        private fun pack(id: Int, localCoords: Int, attributes: Int): Long {
            val shape = attributes shr 2
            val angle = attributes and 0x3
            val localZ = localCoords and 0x3F
            val localX = (localCoords shr 6) and 0x3F
            val level = (localCoords shr 12) and 0x3
            return ((id.toLong() and ID_BIT_MASK) shl ID_BIT_OFFSET) or
                ((shape.toLong() and SHAPE_BIT_MASK) shl SHAPE_BIT_OFFSET) or
                ((angle.toLong() and ANGLE_BIT_MASK) shl ANGLE_BIT_OFFSET) or
                ((localZ.toLong() and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                ((localX.toLong() and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((level.toLong() and LEVEL_BIT_MASK) shl LEVEL_BIT_OFFSET)
        }
    }
}
