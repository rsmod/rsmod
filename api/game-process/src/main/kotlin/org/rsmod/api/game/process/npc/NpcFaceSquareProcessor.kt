package org.rsmod.api.game.process.npc

import org.rsmod.game.entity.Npc
import org.rsmod.game.map.Direction
import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Bounds

public class NpcFaceSquareProcessor {
    public fun process(entity: Npc) {
        entity.processFaceSquare()
    }

    private fun Npc.processFaceSquare() {
        faceAngle = -1
        if (!hasMovedThisCycle && pendingFaceSquare != CoordGrid.NULL) {
            val angle = calculateAngle(pendingFaceSquare, pendingFaceWidth, pendingFaceLength)
            faceAngle = angle ?: -1
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
