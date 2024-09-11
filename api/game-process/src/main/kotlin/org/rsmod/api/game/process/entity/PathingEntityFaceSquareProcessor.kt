package org.rsmod.api.game.process.entity

import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.map.Direction
import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Bounds

public class PathingEntityFaceSquareProcessor {
    public fun process(entity: PathingEntity) {
        entity.processFaceSquare()
    }

    private fun PathingEntity.processFaceSquare() {
        faceAngle = -1
        if (!hasMovedThisCycle && pendingFaceSquare != CoordGrid.NULL) {
            // Seems like face angle is not transmitted if face pathing entity is set on the same
            // cycle. This can be tested by speaking to an NPC (that starts with a chatNpc dialogue)
            // while being cardinally adjacent to them. In this example, the face pathing entity is
            // sent on the first cycle, as well as the face square as you're within op distance.
            // However, the face square is not actually transmitted. This is not the case if you
            // go anywhere that'll take at least 1 cycle to reach op distance. (including being
            // diagonal to the npc)
            if (faceEntitySlot == -1) {
                val angle = calculateAngle(pendingFaceSquare, pendingFaceWidth, pendingFaceLength)
                faceAngle = angle ?: -1
            }
            resetPendingFaceSquare()
        }
    }

    private fun PathingEntity.calculateAngle(target: CoordGrid, width: Int, length: Int): Int? =
        when (target) {
            CoordGrid.ZERO -> 0
            coords -> null
            else -> {
                val targetBounds = Bounds(target, width, length)
                Direction.angleBetween(bounds(), targetBounds)
            }
        }
}
