package org.runerealm.api.game.process.npc

import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.util.EntityFaceAngle
import org.runerealm.map.CoordGrid

public class NpcFaceSquareProcessor {
    public fun process(npc: Npc) {
        npc.processFaceSquare()
    }

    private fun Npc.processFaceSquare() {
        if (!hasMovedThisCycle && pendingFaceSquare != CoordGrid.NULL) {
            val angle = calculateAngle(pendingFaceSquare, pendingFaceWidth, pendingFaceLength)
            pendingFaceAngle = EntityFaceAngle.fromOrNull(angle)
            resetPendingFaceSquare()
        }
    }
}
