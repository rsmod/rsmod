package org.rsmod.plugins.api.update.npc.task

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.model.mob.Npc
import org.rsmod.game.model.mob.NpcList
import org.rsmod.game.model.move.MovementSpeed
import org.rsmod.game.update.task.UpdateTask
import org.rsmod.plugins.api.model.angle
import org.rsmod.plugins.api.update.pollSteps
import org.rsmod.plugins.api.update.speed
import javax.inject.Inject

class NpcPreUpdateTask @Inject constructor(
    private val npcList: NpcList,
    private val collision: CollisionMap
) : UpdateTask {

    override suspend fun execute() {
        npcList.forEach { npc ->
            if (npc == null) {
                return@forEach
            }
            npc.processMovement()
        }
    }

    private fun Npc.processMovement() {
        pollSteps()
        if (!displace && movement.nextSteps.isNotEmpty()) {
            updateMovementSpeed()
        }
    }

    private fun Npc.pollSteps() {
        if (movement.isEmpty()) return
        movement.pollSteps(coords, speed(), collision)
        val lastStep = movement.nextSteps.lastOrNull() ?: return
        coords = lastStep.dest
        orientation = lastStep.dir.angle
    }

    private fun Npc.updateMovementSpeed() {
        val movementSpeed = if (movement.nextSteps.size <= 1) MovementSpeed.Walk else MovementSpeed.Run
        if (movementSpeed != lastSpeed) {
            lastSpeed = movementSpeed
        }
    }
}
