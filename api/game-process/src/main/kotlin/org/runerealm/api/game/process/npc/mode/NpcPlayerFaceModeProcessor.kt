package org.runerealm.api.game.process.npc.mode

import jakarta.inject.Inject
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList

public class NpcPlayerFaceModeProcessor @Inject constructor(private val playerList: PlayerList) {
    private val Npc.maxRange: Int
        get() = type.maxRange

    public fun process(npc: Npc) {
        val interacting = npc.facingTarget(playerList)
        if (interacting == null || !npc.inDistance(interacting)) {
            npc.resetMode()
        } else {
            npc.abortRoute()
        }
    }

    private fun Npc.inDistance(target: Player): Boolean = isWithinDistance(target, maxRange)
}
