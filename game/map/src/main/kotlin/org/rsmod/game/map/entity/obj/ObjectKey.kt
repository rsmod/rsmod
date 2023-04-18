package org.rsmod.game.map.entity.obj

@JvmInline
public value class ObjectKey(public val packed: Byte) {

    public val x: Int get() = (packed.toInt() shr X_BIT_OFFSET) and X_BIT_MASK

    public val z: Int get() = (packed.toInt() shr Z_BIT_OFFSET) and Z_BIT_MASK

    public val slot: Int get() = (packed.toInt() shr SLOT_BIT_OFFSET) and SLOT_BIT_MASK

    public constructor(x: Int, z: Int, slot: Int) : this(pack(x, z, slot))

    public operator fun component1(): Int = x

    public operator fun component2(): Int = z

    public operator fun component3(): Int = slot

    override fun toString(): String {
        return "ObjectKey(x=$x, z=$z, slot=$slot)"
    }

    @Suppress("MemberVisibilityCanBePrivate")
    public companion object {

        public const val X_BIT_COUNT: Int = 3
        public const val X_BIT_MASK: Int = (1 shl X_BIT_COUNT) - 1

        public const val Z_BIT_COUNT: Int = 3
        public const val Z_BIT_MASK: Int = (1 shl Z_BIT_COUNT) - 1

        public const val SLOT_BIT_COUNT: Int = 2
        public const val SLOT_BIT_MASK: Int = (1 shl SLOT_BIT_COUNT) - 1

        public const val Z_BIT_OFFSET: Int = 0
        public const val X_BIT_OFFSET: Int = Z_BIT_COUNT
        public const val SLOT_BIT_OFFSET: Int = X_BIT_COUNT + Z_BIT_COUNT

        private fun pack(x: Int, z: Int, slot: Int): Byte {
            if (x !in 0..X_BIT_MASK) {
                throw IllegalArgumentException("`x` value must be within range [0..$X_BIT_MASK].")
            } else if (z !in 0..Z_BIT_MASK) {
                throw IllegalArgumentException("`z` value must be within range [0..$Z_BIT_MASK].")
            } else if (slot !in 0..SLOT_BIT_MASK) {
                throw IllegalArgumentException("`slot` value must be within range [0..$SLOT_BIT_MASK].")
            }
            val packed = ((x and X_BIT_MASK) shl X_BIT_OFFSET) or
                ((z and Z_BIT_MASK) shl Z_BIT_OFFSET) or
                ((slot and SLOT_BIT_MASK) shl SLOT_BIT_OFFSET)
            return packed.toByte()
        }
    }
}
