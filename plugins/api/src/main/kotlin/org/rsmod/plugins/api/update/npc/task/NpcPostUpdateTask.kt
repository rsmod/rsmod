package org.rsmod.plugins.api.update.npc.task

import org.rsmod.game.model.mob.NpcList
import org.rsmod.game.update.task.UpdateTask
import javax.inject.Inject

class NpcPostUpdateTask @Inject constructor(
    private val npcList: NpcList
) : UpdateTask {

    override suspend fun execute() {
        npcList.forEach { npc ->
            if (npc == null) {
                return@forEach
            }
            npc.entity.updates.clear()
            npc.movement.nextSteps.clear()
            npc.displace = false
        }
    }
}
