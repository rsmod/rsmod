package org.rsmod.game.hit

import org.rsmod.game.entity.util.PathingEntityCommon

@JvmInline
public value class Hitmark(public val packed: Long) {
    /** The hitmark id shown to the entity (player) that took the hit. */
    public val self: Int
        get() = ((packed shr HITMARK_SELF_BIT_OFFSET) and HITMARK_ID_BIT_MASK).toInt()

    /** The hitmark id shown to the entity (player) that dealt the hit. */
    public val source: Int
        get() = ((packed shr HITMARK_SOURCE_BIT_OFFSET) and HITMARK_ID_BIT_MASK).toInt()

    /** The hitmark id shown to other players that see the hit. */
    public val public: Int
        get() = ((packed shr HITMARK_PUBLIC_BIT_OFFSET) and HITMARK_ID_BIT_MASK).toInt()

    /** Returns `true` when hitmark should only be visible to "self" and "source." */
    public val isPrivate: Boolean
        get() = public == HITMARK_ID_BIT_MASK.toInt()

    public val damage: Int
        get() = ((packed shr DAMAGE_BIT_OFFSET) and DAMAGE_BIT_MASK).toInt()

    /** The client delay to show the hit _after_ hitmark has been transmitted. (20ms per value) */
    public val delay: Int
        get() = ((packed shr DELAY_BIT_OFFSET) and DELAY_BIT_MASK).toInt()

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
        source: Int,
        public: Int?,
        damage: Int,
        delay: Int,
        sourceSlot: Int,
    ) : this(pack(self, source, public ?: HITMARK_ID_BIT_MASK.toInt(), damage, delay, sourceSlot))

    public fun copy(
        self: Int = this.self,
        source: Int = this.source,
        public: Int? = this.public,
        damage: Int = this.damage,
        delay: Int = this.delay,
        sourceSlot: Int = this.sourceSlot,
    ): Hitmark = Hitmark(self, source, public, damage, delay, sourceSlot)

    override fun toString(): String =
        "Hitmark(" +
            "damage=$damage, " +
            "clientDelay=$delay, " +
            "selfHitmark=$self, " +
            "sourceHitmark=$source, " +
            "publicHitmark=$public, " +
            "playerSlot=${if (isPlayerSource) playerSlot else null}, " +
            "npcSlot=${if (isNpcSource) npcSlot else null}, " +
            "sourceSlot=$sourceSlot, " +
            "packed=$packed" +
            ")"

    public companion object {
        public const val HITMARK_ID_BIT_COUNT: Int = 8 // 3 hitmark ids represented; 24 total bits.
        public const val DAMAGE_BIT_COUNT: Int = 15
        public const val DELAY_BIT_COUNT: Int = 8
        public const val SOURCE_INDEX_BIT_COUNT: Int = 17

        private const val HITMARK_SELF_BIT_OFFSET: Int = 0
        private const val HITMARK_SOURCE_BIT_OFFSET = HITMARK_SELF_BIT_OFFSET + HITMARK_ID_BIT_COUNT
        private const val HITMARK_PUBLIC_BIT_OFFSET =
            HITMARK_SOURCE_BIT_OFFSET + HITMARK_ID_BIT_COUNT
        private const val DAMAGE_BIT_OFFSET = HITMARK_PUBLIC_BIT_OFFSET + HITMARK_ID_BIT_COUNT
        private const val DELAY_BIT_OFFSET = DAMAGE_BIT_OFFSET + DAMAGE_BIT_COUNT
        private const val SOURCE_SLOT_BIT_OFFSET = DELAY_BIT_OFFSET + DELAY_BIT_COUNT

        public const val HITMARK_ID_BIT_MASK: Long = (1L shl HITMARK_ID_BIT_COUNT) - 1
        public const val DAMAGE_BIT_MASK: Long = (1L shl DAMAGE_BIT_COUNT) - 1
        public const val DELAY_BIT_MASK: Long = (1L shl DELAY_BIT_COUNT) - 1
        public const val SOURCE_SLOT_BIT_MASK: Long = (1L shl SOURCE_INDEX_BIT_COUNT) - 1

        private fun pack(
            self: Int,
            source: Int,
            public: Int,
            damage: Int,
            delay: Int,
            sourceSlot: Int,
        ): Long {
            require(self in 0..HITMARK_ID_BIT_MASK) {
                "`self` must be between [0..$HITMARK_ID_BIT_MASK]. (self=$self)"
            }

            require(source in 0..HITMARK_ID_BIT_MASK) {
                "`source` must be between [0..$HITMARK_ID_BIT_MASK]. (source=$source)"
            }

            require(public in 0..HITMARK_ID_BIT_MASK) {
                "`public` must be between [0..$HITMARK_ID_BIT_MASK]. (public=$public)"
            }

            require(damage in 0..DAMAGE_BIT_MASK) {
                "`damage` must be between [0..$DAMAGE_BIT_MASK]. (damage=$damage)"
            }

            require(delay in 0..DELAY_BIT_MASK) {
                "`delay` must be between [0..$DELAY_BIT_MASK]. (delay=$delay)"
            }

            require(sourceSlot in 0..SOURCE_SLOT_BIT_MASK) {
                "`sourceSlot` must be between [0..$SOURCE_SLOT_BIT_MASK]. (sourceSlot=$sourceSlot)"
            }

            return ((self.toLong() and HITMARK_ID_BIT_MASK) shl HITMARK_SELF_BIT_OFFSET) or
                ((source.toLong() and HITMARK_ID_BIT_MASK) shl HITMARK_SOURCE_BIT_OFFSET) or
                ((public.toLong() and HITMARK_ID_BIT_MASK) shl HITMARK_PUBLIC_BIT_OFFSET) or
                ((damage.toLong() and DAMAGE_BIT_MASK) shl DAMAGE_BIT_OFFSET) or
                ((delay.toLong() and DELAY_BIT_MASK) shl DELAY_BIT_OFFSET) or
                ((sourceSlot.toLong() and SOURCE_SLOT_BIT_MASK) shl SOURCE_SLOT_BIT_OFFSET)
        }

        public fun fromNoSource(
            self: Int,
            source: Int,
            public: Int?,
            damage: Int,
            delay: Int,
        ): Hitmark =
            Hitmark(
                self = self,
                source = source,
                public = public,
                damage = damage,
                delay = delay,
                sourceSlot = SOURCE_SLOT_BIT_MASK.toInt(),
            )

        public fun fromNpcSource(
            self: Int,
            source: Int,
            public: Int?,
            damage: Int,
            delay: Int,
            slotId: Int,
        ): Hitmark =
            Hitmark(
                self = self,
                source = source,
                public = public,
                damage = damage,
                delay = delay,
                sourceSlot = slotId,
            )

        public fun fromPlayerSource(
            self: Int,
            source: Int,
            public: Int?,
            damage: Int,
            delay: Int,
            slotId: Int,
        ): Hitmark =
            Hitmark(
                self = self,
                source = source,
                public = public,
                damage = damage,
                delay = delay,
                sourceSlot = slotId + (PathingEntityCommon.INTERNAL_NPC_LIMIT + 1),
            )
    }
}
