package org.rsmod.game.entity.util

import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

@JvmInline
public value class EntityFaceTarget(public val entitySlot: Int) {
    public val playerSlot: Int
        get() = entitySlot - FACE_PLAYER_START_SLOT

    public val isPlayer: Boolean
        get() = entitySlot >= FACE_PLAYER_START_SLOT

    public val npcSlot: Int
        get() = entitySlot

    public val isNpc: Boolean
        get() = entitySlot in 0 until FACE_PLAYER_START_SLOT

    public constructor(target: Player) : this(target.slotId or FACE_PLAYER_START_SLOT)

    public constructor(target: Npc) : this(target.slotId)

    public companion object {
        public val NULL: EntityFaceTarget = EntityFaceTarget(-1)

        private const val FACE_PLAYER_START_SLOT: Int = PathingEntityCommon.INTERNAL_NPC_LIMIT + 1
    }
}
