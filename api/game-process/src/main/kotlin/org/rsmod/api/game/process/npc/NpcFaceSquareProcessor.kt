package org.rsmod.api.game.process.npc

import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.util.EntityFaceAngle
import org.rsmod.game.map.Direction
import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Bounds

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

    private fun Npc.calculateAngle(target: CoordGrid, width: Int, length: Int): Int? =
        when (target) {
            CoordGrid.ZERO -> 0
            coords -> null
            else -> {
                val targetBounds = Bounds(target, width, length)
                Direction.angleBetween(bounds(), targetBounds)
            }
        }
}
