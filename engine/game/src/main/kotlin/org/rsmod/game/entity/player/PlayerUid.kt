package org.rsmod.game.entity.player

@JvmInline
public value class PlayerUid(public val packed: Int) {
    public val slotId: Int
        get() = (packed shr SLOT_BIT_OFFSET) and SLOT_BIT_MASK

    public val uuid: Int
        get() = (packed shr UUID_BIT_OFFSET) and UUID_BIT_MASK

    public constructor(slot: Int, uuid: Long) : this(pack(slot, uuid.extractUuidBits()))

    public companion object {
        public val NULL: PlayerUid = PlayerUid(-1)

        public const val SLOT_BIT_COUNT: Int = 11
        public const val UUID_BIT_COUNT: Int = 21

        public const val SLOT_BIT_OFFSET: Int = 0
        public const val UUID_BIT_OFFSET: Int = SLOT_BIT_OFFSET + SLOT_BIT_COUNT

        public const val SLOT_BIT_MASK: Int = (1 shl SLOT_BIT_COUNT) - 1
        public const val UUID_BIT_MASK: Int = (1 shl UUID_BIT_COUNT) - 1

        private fun pack(slot: Int, uuid: Int): Int {
            require(slot in 0..SLOT_BIT_MASK) { "`slot` must be within [0..$SLOT_BIT_MASK]." }
            require(uuid in 0..UUID_BIT_MASK) { "`uuid` must be within [0..$UUID_BIT_MASK]." }
            return ((slot and SLOT_BIT_MASK) shl SLOT_BIT_OFFSET) or
                ((uuid and UUID_BIT_MASK) shl UUID_BIT_OFFSET)
        }

        private fun Long.extractUuidBits(): Int = (this and UUID_BIT_MASK.toLong()).toInt()
    }
}
