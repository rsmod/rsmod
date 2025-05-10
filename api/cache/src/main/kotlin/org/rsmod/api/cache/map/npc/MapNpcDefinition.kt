package org.rsmod.api.cache.map.npc

@JvmInline
public value class MapNpcDefinition(public val packed: Int) {
    public val id: Int
        get() = (packed shr ID_BIT_OFFSET) and ID_BIT_MASK

    public val localX: Int
        get() = (packed shr X_BIT_OFFSET) and X_BIT_MASK

    public val localZ: Int
        get() = (packed shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val level: Int
        get() = (packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK

    public constructor(
        id: Int,
        localX: Int,
        localZ: Int,
        level: Int,
    ) : this(pack(id, localX, localZ, level))

    override fun toString(): String {
        return "MapNpcDefinition(id=$id, localX=$localX, localZ=$localZ, level=$level)"
    }

    private companion object {
        private const val ID_BIT_COUNT = 16
        private const val Z_BIT_COUNT = 6
        private const val X_BIT_COUNT = 6
        private const val LEVEL_BIT_COUNT = 2

        private const val ID_BIT_OFFSET = 0
        private const val Z_BIT_OFFSET = ID_BIT_OFFSET + ID_BIT_COUNT
        private const val X_BIT_OFFSET = Z_BIT_OFFSET + Z_BIT_COUNT
        private const val LEVEL_BIT_OFFSET = X_BIT_OFFSET + X_BIT_COUNT

        private const val ID_BIT_MASK = (1 shl ID_BIT_COUNT) - 1
        private const val Z_BIT_MASK = (1 shl Z_BIT_COUNT) - 1
        private const val X_BIT_MASK = (1 shl X_BIT_COUNT) - 1
        private const val LEVEL_BIT_MASK = (1 shl LEVEL_BIT_COUNT) - 1

        private fun pack(id: Int, localX: Int, localZ: Int, level: Int): Int {
            require(id in 0..ID_BIT_MASK) {
                "`id` value must be within range [0..${ID_BIT_MASK}]. (id=$id)"
            }
            return ((id and ID_BIT_MASK) shl ID_BIT_OFFSET) or
                ((localZ and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                ((localX and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((level and LEVEL_BIT_MASK) shl LEVEL_BIT_OFFSET)
        }
    }
}
