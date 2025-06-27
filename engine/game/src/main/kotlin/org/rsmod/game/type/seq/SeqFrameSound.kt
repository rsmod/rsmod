package org.rsmod.game.type.seq

@JvmInline
public value class SeqFrameSound(public val packed: Long) {
    public val type: Int
        get() = ((packed shr TYPE_BIT_OFFSET) and TYPE_BIT_MASK).toInt()

    public val weight: Int
        get() = ((packed shr WEIGHT_BIT_OFFSET) and WEIGHT_BIT_MASK).toInt()

    public val loops: Int
        get() = ((packed shr LOOPS_BIT_OFFSET) and LOOPS_BIT_MASK).toInt()

    public val range: Int
        get() = ((packed shr RANGE_BIT_OFFSET) and RANGE_BIT_MASK).toInt()

    public val size: Int
        get() = ((packed shr SIZE_BIT_OFFSET) and SIZE_BIT_MASK).toInt()

    public constructor(
        type: Int,
        weight: Int,
        loops: Int,
        range: Int,
        size: Int,
    ) : this(pack(type, loops, range, size, weight))

    public companion object {
        public val NULL: SeqFrameSound = SeqFrameSound(-1)

        public const val TYPE_BIT_COUNT: Int = 16
        public const val LOOPS_BIT_COUNT: Int = 8
        public const val RANGE_BIT_COUNT: Int = 8
        public const val SIZE_BIT_COUNT: Int = 8
        public const val WEIGHT_BIT_COUNT: Int = 8

        public const val TYPE_BIT_OFFSET: Int = 0
        public const val LOOPS_BIT_OFFSET: Int = TYPE_BIT_OFFSET + TYPE_BIT_COUNT
        public const val RANGE_BIT_OFFSET: Int = LOOPS_BIT_OFFSET + LOOPS_BIT_COUNT
        public const val SIZE_BIT_OFFSET: Int = RANGE_BIT_OFFSET + RANGE_BIT_COUNT
        public const val WEIGHT_BIT_OFFSET: Int = SIZE_BIT_OFFSET + SIZE_BIT_COUNT

        public const val TYPE_BIT_MASK: Long = (1L shl TYPE_BIT_COUNT) - 1
        public const val LOOPS_BIT_MASK: Long = (1L shl LOOPS_BIT_COUNT) - 1
        public const val RANGE_BIT_MASK: Long = (1L shl RANGE_BIT_COUNT) - 1
        public const val SIZE_BIT_MASK: Long = (1L shl SIZE_BIT_COUNT) - 1
        public const val WEIGHT_BIT_MASK: Long = (1L shl WEIGHT_BIT_COUNT) - 1

        private fun pack(type: Int, weight: Int, loops: Int, range: Int, size: Int): Long {
            require(type in 0..TYPE_BIT_MASK) {
                "`type` value must be within range [0..$TYPE_BIT_MASK]."
            }
            require(weight in 0..WEIGHT_BIT_MASK) {
                "`weight` value must be within range [0..$WEIGHT_BIT_MASK]."
            }
            require(loops in 0..LOOPS_BIT_MASK) {
                "`loops` value must be within range [0..$LOOPS_BIT_MASK]."
            }
            require(range in 0..RANGE_BIT_MASK) {
                "`range` value must be within range [0..$RANGE_BIT_MASK]."
            }
            require(size in 0..SIZE_BIT_MASK) {
                "`size` value must be within range [0..$SIZE_BIT_MASK]."
            }
            return ((type.toLong() and TYPE_BIT_MASK) shl TYPE_BIT_OFFSET) or
                ((loops.toLong() and LOOPS_BIT_MASK) shl LOOPS_BIT_OFFSET) or
                ((range.toLong() and RANGE_BIT_MASK) shl RANGE_BIT_OFFSET) or
                ((size.toLong() and SIZE_BIT_MASK) shl SIZE_BIT_OFFSET) or
                ((weight.toLong() and WEIGHT_BIT_MASK) shl WEIGHT_BIT_OFFSET)
        }
    }
}
