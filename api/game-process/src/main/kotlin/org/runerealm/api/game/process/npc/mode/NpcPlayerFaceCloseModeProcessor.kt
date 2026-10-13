package org.runerealm.api.game.process.npc.mode

import jakarta.inject.Inject
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList

public class NpcPlayerFaceCloseModeProcessor
@Inject
constructor(private val playerList: PlayerList) {
    public fun process(npc: Npc) {
        val interacting = npc.facingTarget(playerList)
        if (interacting == null || !npc.inCloseDistance(interacting)) {
            npc.resetMode()
        } else {
            npc.abortRoute()
        }
    }

    private fun Npc.inCloseDistance(target: Player): Boolean = isWithinDistance(target, 1)
}
