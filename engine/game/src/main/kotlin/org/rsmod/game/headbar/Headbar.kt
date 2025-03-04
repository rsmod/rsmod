package org.rsmod.game.headbar

import org.rsmod.game.entity.util.PathingEntityCommon

@JvmInline
public value class Headbar(public val packed: Long) {
    /** The headbar id shown to the entity (player) that took the hit. */
    public val self: Int
        get() = ((packed shr HEADBAR_SELF_BIT_OFFSET) and HEADBAR_ID_BIT_MASK).toInt()

    /** The headbar id shown to other players that see the hit. */
    public val public: Int
        get() = ((packed shr HEADBAR_PUBLIC_BIT_OFFSET) and HEADBAR_ID_BIT_MASK).toInt()

    /** Returns `true` when the headbar should only be visible to "self." */
    public val isPrivate: Boolean
        get() = public == HEADBAR_ID_BIT_MASK.toInt()

    public val startFill: Int
        get() = ((packed shr START_FILL_BIT_OFFSET) and START_FILL_BIT_MASK).toInt()

    public val endFill: Int
        get() = ((packed shr END_FILL_BIT_OFFSET) and END_FILL_BIT_MASK).toInt()

    public val startTime: Int
        get() = ((packed shr START_TIME_BIT_OFFSET) and START_TIME_BIT_MASK).toInt()

    public val endTime: Int
        get() = ((packed shr END_TIME_BIT_OFFSET) and END_TIME_BIT_MASK).toInt()

    public val sourceSlot: Int
        get() = ((packed shr SOURCE_SLOT_BIT_OFFSET) and SOURCE_SLOT_BIT_MASK).toInt()

    public val isNoSource: Boolean
        get() = sourceSlot == SOURCE_SLOT_BIT_MASK.toInt()

    public val isPlayerSource: Boolean
        get() = sourceSlot > PathingEntityCommon.INTERNAL_NPC_LIMIT && !isNoSource

    public val playerSlot: Int
        get() = sourceSlot - (PathingEntityCommon.INTERNAL_NPC_LIMIT + 1)

    public val isNpcSource: Boolean
        get() = sourceSlot in 0..PathingEntityCommon.INTERNAL_NPC_LIMIT

    public val npcSlot: Int
        get() = sourceSlot

    private constructor(
        self: Int,
        public: Int?,
        startFill: Int,
        endFill: Int,
        startTime: Int,
        endTime: Int,
        sourceSlot: Int,
    ) : this(
        pack(
            self,
            public ?: HEADBAR_ID_BIT_MASK.toInt(),
            startFill,
            endFill,
            startTime,
            endTime,
            sourceSlot,
        )
    )

    override fun toString(): String =
        "Headbar(" +
            "startFill=$startFill, " +
            "endFill=$endFill, " +
            "startTime=$startTime, " +
            "endTime=$endTime, " +
            "selfHeadbar=$self, " +
            "publicHeadbar=$public, " +
            "npcSlot=${if (isNpcSource) npcSlot else null}, " +
            "playerSlot=${if (isPlayerSource) playerSlot else null}, " +
            "sourceSlot=$sourceSlot, " +
            "packed=$packed" +
            ")"

    public companion object {
        public const val HEADBAR_ID_BIT_COUNT: Int = 8 // 2 headbar ids represented; 16 total bits.
        public const val START_FILL_BIT_COUNT: Int = 8
        public const val END_FILL_BIT_COUNT: Int = 8
        public const val START_TIME_BIT_COUNT: Int = 7
        public const val END_TIME_BIT_COUNT: Int = 8
        public const val SOURCE_SLOT_BIT_COUNT: Int = 17

        private const val HEADBAR_SELF_BIT_OFFSET = 0
        private const val HEADBAR_PUBLIC_BIT_OFFSET = HEADBAR_SELF_BIT_OFFSET + HEADBAR_ID_BIT_COUNT
        private const val START_FILL_BIT_OFFSET = HEADBAR_PUBLIC_BIT_OFFSET + HEADBAR_ID_BIT_COUNT
        private const val END_FILL_BIT_OFFSET = START_FILL_BIT_OFFSET + START_FILL_BIT_COUNT
        private const val START_TIME_BIT_OFFSET = END_FILL_BIT_OFFSET + END_FILL_BIT_COUNT
        private const val END_TIME_BIT_OFFSET = START_TIME_BIT_OFFSET + START_TIME_BIT_COUNT
        private const val SOURCE_SLOT_BIT_OFFSET = END_TIME_BIT_OFFSET + END_TIME_BIT_COUNT

        public const val HEADBAR_ID_BIT_MASK: Long = (1L shl HEADBAR_ID_BIT_COUNT) - 1
        public const val START_FILL_BIT_MASK: Long = (1L shl START_FILL_BIT_COUNT) - 1
        public const val END_FILL_BIT_MASK: Long = (1L shl END_FILL_BIT_COUNT) - 1
        public const val START_TIME_BIT_MASK: Long = (1L shl START_TIME_BIT_COUNT) - 1
        public const val END_TIME_BIT_MASK: Long = (1L shl END_TIME_BIT_COUNT) - 1
        public const val SOURCE_SLOT_BIT_MASK: Long = (1L shl SOURCE_SLOT_BIT_COUNT) - 1

        private fun pack(
            self: Int,
            public: Int,
            startFill: Int,
            endFill: Int,
            startTime: Int,
            endTime: Int,
            sourceSlot: Int,
        ): Long {
            require(self in 0..HEADBAR_ID_BIT_MASK) {
                "`self` must be between [0..$HEADBAR_ID_BIT_MASK]. (self=$self)"
            }

            require(public in 0..HEADBAR_ID_BIT_MASK) {
                "`public` must be between [0..$HEADBAR_ID_BIT_MASK]. (public=$public)"
            }

            require(startFill in 0..START_FILL_BIT_MASK) {
                "`startFill` must be between [0..$START_FILL_BIT_MASK]. (startFill=$startFill)"
            }

            require(endFill in 0..END_FILL_BIT_MASK) {
                "`endFill` must be between [0..$END_FILL_BIT_MASK]. (endFill=$endFill)"
            }

            require(startTime in 0..START_TIME_BIT_MASK) {
                "`startTime` must be between [0..$START_TIME_BIT_MASK]. (startTime=$startTime)"
            }

            require(endTime in 0..END_TIME_BIT_MASK) {
                "`endTime` must be between [0..$END_TIME_BIT_MASK]. (endTime=$endTime)"
            }

            require(sourceSlot in 0..SOURCE_SLOT_BIT_MASK) {
                "`sourceSlot` must be between [0..$SOURCE_SLOT_BIT_MASK]. (sourceSlot=$sourceSlot)"
            }

            return ((self.toLong() and HEADBAR_ID_BIT_MASK) shl HEADBAR_SELF_BIT_OFFSET) or
                ((public.toLong() and HEADBAR_ID_BIT_MASK) shl HEADBAR_PUBLIC_BIT_OFFSET) or
                ((startFill.toLong() and START_FILL_BIT_MASK) shl START_FILL_BIT_OFFSET) or
                ((endFill.toLong() and END_FILL_BIT_MASK) shl END_FILL_BIT_OFFSET) or
                ((startTime.toLong() and START_TIME_BIT_MASK) shl START_TIME_BIT_OFFSET) or
                ((endTime.toLong() and END_TIME_BIT_MASK) shl END_TIME_BIT_OFFSET) or
                ((sourceSlot.toLong() and SOURCE_SLOT_BIT_MASK) shl SOURCE_SLOT_BIT_OFFSET)
        }

        public fun fromNoSource(
            self: Int,
            public: Int?,
            startFill: Int,
            endFill: Int,
            startTime: Int,
            endTime: Int,
        ): Headbar =
            Headbar(
                self = self,
                public = public,
                startFill = startFill,
                endFill = endFill,
                startTime = startTime,
                endTime = endTime,
                sourceSlot = SOURCE_SLOT_BIT_MASK.toInt(),
            )

        public fun fromNpcSource(
            self: Int,
            public: Int?,
            startFill: Int,
            endFill: Int,
            startTime: Int,
            endTime: Int,
            slotId: Int,
        ): Headbar =
            Headbar(
                self = self,
                public = public,
                startFill = startFill,
                endFill = endFill,
                startTime = startTime,
                endTime = endTime,
                sourceSlot = slotId,
            )

        public fun fromPlayerSource(
            self: Int,
            public: Int?,
            startFill: Int,
            endFill: Int,
            startTime: Int,
            endTime: Int,
            slotId: Int,
        ): Headbar =
            Headbar(
                self = self,
                public = public,
                startFill = startFill,
                endFill = endFill,
                startTime = startTime,
                endTime = endTime,
                sourceSlot = slotId + (PathingEntityCommon.INTERNAL_NPC_LIMIT + 1),
            )
    }
}
