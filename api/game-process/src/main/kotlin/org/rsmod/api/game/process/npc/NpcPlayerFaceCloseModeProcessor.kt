package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList

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
