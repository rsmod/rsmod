package org.rsmod.game.spot

@JvmInline
public value class EntitySpotanim(public val packed: Long) {
    public val id: Int
        get() = ((packed shr ID_BIT_OFFSET) and ID_BIT_MASK).toInt()

    public val delay: Int
        get() = ((packed shr DELAY_BIT_OFFSET) and DELAY_BIT_MASK).toInt()

    public val height: Int
        get() = ((packed shr HEIGHT_BIT_OFFSET) and HEIGHT_BIT_MASK).toInt()

    public val slot: Int
        get() = ((packed shr SLOT_BIT_OFFSET) and SLOT_BIT_MASK).toInt()

    public constructor(
        id: Int,
        delay: Int,
        height: Int,
        slot: Int,
    ) : this(pack(id, delay, height, slot))

    public operator fun component1(): Int = id

    public operator fun component2(): Int = delay

    public operator fun component3(): Int = height

    public operator fun component4(): Int = slot

    override fun toString(): String =
        "EntitySpotanim(id=$id, delay=$delay, height=$height, slot=$slot)"

    public companion object {
        public const val ID_BIT_COUNT: Int = 16
        public const val DELAY_BIT_COUNT: Int = 16
        public const val HEIGHT_BIT_COUNT: Int = 16
        public const val SLOT_BIT_COUNT: Int = 16

        public const val ID_BIT_OFFSET: Int = 0
        public const val DELAY_BIT_OFFSET: Int = ID_BIT_OFFSET + ID_BIT_COUNT
        public const val HEIGHT_BIT_OFFSET: Int = DELAY_BIT_OFFSET + DELAY_BIT_COUNT
        public const val SLOT_BIT_OFFSET: Int = HEIGHT_BIT_OFFSET + HEIGHT_BIT_COUNT

        public const val ID_BIT_MASK: Long = (1L shl ID_BIT_COUNT) - 1
        public const val DELAY_BIT_MASK: Long = (1L shl DELAY_BIT_COUNT) - 1
        public const val HEIGHT_BIT_MASK: Long = (1L shl HEIGHT_BIT_COUNT) - 1
        public const val SLOT_BIT_MASK: Long = (1L shl SLOT_BIT_COUNT) - 1

        private fun pack(id: Int, delay: Int, height: Int, slot: Int): Long {
            require(id in 0..ID_BIT_MASK) { "`id` value must be within range [0..$ID_BIT_MASK]." }
            require(delay in 0..DELAY_BIT_MASK) {
                "`delay` value must be within range [0..$DELAY_BIT_MASK]."
            }
            require(height in 0..HEIGHT_BIT_MASK) {
                "`height` value must be within range [0..$HEIGHT_BIT_MASK]."
            }
            require(slot in 0..SLOT_BIT_MASK) {
                "`slot` value must be within range [0..$SLOT_BIT_MASK]."
            }
            return ((id.toLong() and ID_BIT_MASK) shl ID_BIT_OFFSET) or
                ((delay.toLong() and DELAY_BIT_MASK) shl DELAY_BIT_OFFSET) or
                ((height.toLong() and HEIGHT_BIT_MASK) shl HEIGHT_BIT_OFFSET) or
                ((slot.toLong() and SLOT_BIT_MASK) shl SLOT_BIT_OFFSET)
        }
    }
}
