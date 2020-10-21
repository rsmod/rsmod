package gg.rsmod.plugins.core.update.player

import com.google.inject.Inject
import gg.rsmod.game.model.mob.PlayerList
import gg.rsmod.game.update.task.UpdateTask

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
            player.appendTeleport = false
            player.snapshot = player.snapshot()
        }
    }
}
