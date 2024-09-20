package org.rsmod.game.seq

@JvmInline
public value class EntitySeq(public val packed: Int) {
    public val id: Int
        get() = (packed shr ID_BIT_OFFSET) and ID_BIT_MASK

    public val delay: Int
        get() = (packed shr DELAY_BIT_OFFSET) and DELAY_BIT_MASK

    public val priority: Int
        get() = (packed shr PRIORITY_BIT_OFFSET) and PRIORITY_BIT_MASK

    public constructor(id: Int, delay: Int, priority: Int) : this(pack(id, delay, priority))

    public operator fun component1(): Int = id

    public operator fun component2(): Int = delay

    public operator fun component3(): Int = priority

    override fun toString(): String = "EntitySeq(id=$id, delay=$delay, priority=$priority)"

    public companion object {
        public val ZERO: EntitySeq = EntitySeq(0)
        public val NULL: EntitySeq = EntitySeq(-1)

        public const val ID_BIT_COUNT: Int = 16
        public const val DELAY_BIT_COUNT: Int = 8
        public const val PRIORITY_BIT_COUNT: Int = 8

        public const val ID_BIT_OFFSET: Int = 0
        public const val DELAY_BIT_OFFSET: Int = ID_BIT_OFFSET + ID_BIT_COUNT
        public const val PRIORITY_BIT_OFFSET: Int = DELAY_BIT_OFFSET + DELAY_BIT_COUNT

        public const val ID_BIT_MASK: Int = (1 shl ID_BIT_COUNT) - 1
        public const val DELAY_BIT_MASK: Int = (1 shl DELAY_BIT_COUNT) - 1
        public const val PRIORITY_BIT_MASK: Int = (1 shl PRIORITY_BIT_COUNT) - 1

        private fun pack(id: Int, delay: Int, angle: Int): Int {
            require(id in 0..ID_BIT_MASK) { "`id` value must be within range [0..$ID_BIT_MASK]." }
            require(delay in 0..DELAY_BIT_MASK) {
                "`delay` value must be within range [0..$DELAY_BIT_MASK]."
            }
            require(angle in 0..PRIORITY_BIT_MASK) {
                "`priority` value must be within range [0..$PRIORITY_BIT_MASK]."
            }
            return ((id and ID_BIT_MASK) shl ID_BIT_OFFSET) or
                ((delay and DELAY_BIT_MASK) shl DELAY_BIT_OFFSET) or
                ((angle and PRIORITY_BIT_MASK) shl PRIORITY_BIT_OFFSET)
        }
    }
}
