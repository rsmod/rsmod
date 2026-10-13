package org.runerealm.api.game.process.npc.mode

import jakarta.inject.Inject
import org.runerealm.api.npc.interact.AiPlayerInteractions
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList
import org.runerealm.game.interact.InteractionOp
import org.runerealm.game.interact.InteractionPlayer

public class AiPlayerModeProcessor
@Inject
constructor(private val playerList: PlayerList, private val interactions: AiPlayerInteractions) {
    public fun processOp(npc: Npc, op: InteractionOp) {
        val target = npc.resolveTarget()
        if (target == null) {
            npc.resetMode()
            return
        }
        npc.clearIdleCycles()
        npc.facePlayer(target)
        interactions.interactOp(npc, target, op)
    }

    public fun processAp(npc: Npc, op: InteractionOp) {
        val target = npc.resolveTarget()
        if (target == null) {
            npc.resetMode()
            return
        }
        npc.clearIdleCycles()
        npc.facePlayer(target)
        interactions.interactAp(npc, target, op)
    }

    private fun Npc.resolveTarget(): Player? {
        val interaction = this.interaction ?: return null
        if (interaction !is InteractionPlayer) {
            return null
        }
        return interaction.uid.resolve(playerList)
    }
}
