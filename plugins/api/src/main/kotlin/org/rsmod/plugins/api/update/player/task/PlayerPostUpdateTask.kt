package org.rsmod.plugins.api.update.player.task

import com.google.inject.Inject
import org.rsmod.game.model.mob.PlayerList
import org.rsmod.game.update.task.UpdateTask

class PlayerPostUpdateTask @Inject constructor(
    private val playerList: PlayerList
) : UpdateTask {

    override suspend fun execute() {
        playerList.forEach { player ->
            if (player == null) {
                return@forEach
            }
            player.entity.updates.clear()
            player.movement.clear()
            player.displace = false
            player.snapshot = player.snapshot()
        }
    }
}
