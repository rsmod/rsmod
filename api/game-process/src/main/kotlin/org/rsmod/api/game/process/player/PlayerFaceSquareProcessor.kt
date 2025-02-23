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
            // Seems like face angle is not transmitted if face pathing entity is set on the same
            // cycle. This can be tested by speaking to a npc (that starts with a chatNpc dialogue)
            // while being cardinally adjacent to them. In this example, the face pathing entity is
            // sent on the first cycle, as well as the face square (as you're within op distance).
            // However, the face square is not actually transmitted.
            //
            // This is not the case if you go anywhere that'll take at least 1 cycle to reach op
            // distance. (including being diagonal to the npc)
            if (lastFaceEntity != currentMapClock) {
                val angle = calculateAngle(pendingFaceSquare, pendingFaceWidth, pendingFaceLength)
                pendingFaceAngle = EntityFaceAngle.fromOrNull(angle)
            }
            resetPendingFaceSquare()
        }
    }
}
