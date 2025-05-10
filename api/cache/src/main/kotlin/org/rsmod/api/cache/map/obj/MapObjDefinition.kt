package org.rsmod.api.cache.map.obj

@JvmInline
public value class MapObjDefinition(public val packed: Long) {
    public val id: Int
        get() = ((packed shr ID_BIT_OFFSET) and ID_BIT_MASK).toInt()

    public val localX: Int
        get() = ((packed shr X_BIT_OFFSET) and X_BIT_MASK).toInt()

    public val localZ: Int
        get() = ((packed shr Z_BIT_OFFSET) and Z_BIT_MASK).toInt()

    public val level: Int
        get() = ((packed shr LEVEL_BIT_OFFSET) and LEVEL_BIT_MASK).toInt()

    public val count: Int
        get() = ((packed shr COUNT_BIT_OFFSET) and COUNT_BIT_MASK).toInt()

    public constructor(
        id: Int,
        localX: Int,
        localZ: Int,
        level: Int,
        count: Int,
    ) : this(pack(id, localX, localZ, level, count))

    override fun toString(): String =
        "MapObjDefinition(" +
            "id=$id, " +
            "count=$count, " +
            "localX=$localX, " +
            "localZ=$localZ, " +
            "level=$level" +
            ")"

    private companion object {
        private const val ID_BIT_COUNT = 16
        private const val Z_BIT_COUNT = 6
        private const val X_BIT_COUNT = 6
        private const val LEVEL_BIT_COUNT = 2
        private const val COUNT_BIT_COUNT = 32

        private const val ID_BIT_OFFSET = 0
        private const val Z_BIT_OFFSET = ID_BIT_OFFSET + ID_BIT_COUNT
        private const val X_BIT_OFFSET = Z_BIT_OFFSET + Z_BIT_COUNT
        private const val LEVEL_BIT_OFFSET = X_BIT_OFFSET + X_BIT_COUNT
        private const val COUNT_BIT_OFFSET = LEVEL_BIT_OFFSET + LEVEL_BIT_COUNT

        private const val ID_BIT_MASK = (1L shl ID_BIT_COUNT) - 1
        private const val Z_BIT_MASK = (1L shl Z_BIT_COUNT) - 1
        private const val X_BIT_MASK = (1L shl X_BIT_COUNT) - 1
        private const val LEVEL_BIT_MASK = (1L shl LEVEL_BIT_COUNT) - 1
        private const val COUNT_BIT_MASK = (1L shl COUNT_BIT_COUNT) - 1

        private fun pack(id: Int, localX: Int, localZ: Int, level: Int, count: Int): Long {
            require(id in 0..ID_BIT_MASK) { "`id` must be in range [0..$ID_BIT_MASK]. (id=$id)" }
            return ((id.toLong() and ID_BIT_MASK) shl ID_BIT_OFFSET) or
                ((localZ.toLong() and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                ((localX.toLong() and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((level.toLong() and LEVEL_BIT_MASK) shl LEVEL_BIT_OFFSET) or
                ((count.toLong() and COUNT_BIT_MASK) shl COUNT_BIT_OFFSET)
        }
    }
}
