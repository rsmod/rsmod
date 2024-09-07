package org.rsmod.content.other.generic.ladders

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.type.seq.SeqType
import org.rsmod.map.CoordGrid
import org.rsmod.pathfinder.collision.CollisionFlagMap

object Ladders {
    suspend fun climb(
        collision: CollisionFlagMap,
        access: ProtectedAccess,
        dest: CoordGrid,
        seq: SeqType,
    ) {
        access.anim(seq)
        access.delay(1)
        access.telejump(collision, dest)
    }
}
