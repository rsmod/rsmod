package org.rsmod.api.game.process.player

import org.rsmod.game.entity.Player
import org.rsmod.game.entity.util.EntityFaceAngle
import org.rsmod.map.CoordGrid

public class PlayerFaceSquareProcessor {
    public fun process(player: Player) {
        player.processFaceSquare()
    }

    private fun Player.processFaceSquare() {
        if (!hasMovedThisCycle && pendingFaceSquare != CoordGrid.NULL) {
            // Face angle is not transmitted when `face_pathingentity` is sent on the same cycle.
            // This can be tested by speaking to a npc that starts with a `chatNpc` dialogue
            // while standing cardinally adjacent to them. In this case, since you're within op
            // distance, both the face pathing entity and the face square should be sent on the
            // first cycle. However, only the face pathing entity is sent, and the face square
            // is _not_ transmitted.
            //
            // This does not occur when it takes more than one cycle to reach the npc.
            if (lastFaceEntity != currentMapClock) {
                val angle = calculateAngle(pendingFaceSquare, pendingFaceWidth, pendingFaceLength)
                pendingFaceAngle = EntityFaceAngle.fromOrNull(angle)
            }
            resetPendingFaceSquare()
        }
    }
}
