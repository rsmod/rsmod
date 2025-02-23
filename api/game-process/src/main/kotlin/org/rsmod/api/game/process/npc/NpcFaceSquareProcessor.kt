package org.rsmod.api.game.process.npc

import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.util.EntityFaceAngle
import org.rsmod.map.CoordGrid

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
