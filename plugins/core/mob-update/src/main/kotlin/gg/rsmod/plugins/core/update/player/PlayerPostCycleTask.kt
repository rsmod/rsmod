package gg.rsmod.plugins.core.update.player

import com.google.inject.Inject
import gg.rsmod.game.model.mob.PlayerList
import gg.rsmod.game.model.mob.update.UpdateTask

class PlayerPostCycleTask @Inject constructor(
    private val playerList: PlayerList
) : UpdateTask {

    override fun execute() {
        playerList.forEach { player ->
            if (player == null) {
                return@forEach
            }

        }
    }
}
