package org.rsmod.game.face

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

        // We don't declare or use [org.rsmod.game.entity.NpcList]'s capacity because some
        // developers will more than likely be tempted to fiddle with that npc list capacity
        // at some point. In that case, we do not want the npc list capacity, whether lower
        // or higher, to affect this value used for facing entities.
        private const val NPC_LIMIT = 65535
        private const val FACE_PLAYER_START_SLOT: Int = NPC_LIMIT + 1
    }
}
