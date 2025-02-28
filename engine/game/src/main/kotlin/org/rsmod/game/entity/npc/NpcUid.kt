package org.rsmod.game.entity.npc

@JvmInline
public value class NpcUid(public val packed: Int) {
    public val slotId: Int
        get() = (packed shr SLOT_BIT_OFFSET) and SLOT_BIT_MASK

    public val type: Int
        get() = (packed shr TYPE_BIT_OFFSET) and TYPE_BIT_MASK

    public constructor(slot: Int, type: Int) : this(pack(slot, type))

    public companion object {
        public val NULL: NpcUid = NpcUid(-1)

        public const val SLOT_BIT_COUNT: Int = 16
        public const val TYPE_BIT_COUNT: Int = 16

        public const val SLOT_BIT_OFFSET: Int = 0
        public const val TYPE_BIT_OFFSET: Int = SLOT_BIT_OFFSET + SLOT_BIT_COUNT

        public const val SLOT_BIT_MASK: Int = (1 shl SLOT_BIT_COUNT) - 1
        public const val TYPE_BIT_MASK: Int = (1 shl TYPE_BIT_COUNT) - 1

        private fun pack(slot: Int, type: Int): Int {
            require(slot in 0..SLOT_BIT_MASK) { "`slot` must be within [0..$SLOT_BIT_MASK]." }
            require(type in 0..TYPE_BIT_MASK) { "`type` must be within [0..$TYPE_BIT_MASK]." }
            return ((slot and SLOT_BIT_MASK) shl SLOT_BIT_OFFSET) or
                ((type and TYPE_BIT_MASK) shl TYPE_BIT_OFFSET)
        }
    }
}
