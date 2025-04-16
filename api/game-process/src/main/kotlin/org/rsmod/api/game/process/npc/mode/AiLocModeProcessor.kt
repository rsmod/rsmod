package org.rsmod.api.game.process.npc.mode

import jakarta.inject.Inject
import org.rsmod.api.npc.interact.AiLocInteractions
import org.rsmod.game.entity.Npc
import org.rsmod.game.interact.InteractionLoc
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.loc.BoundLocInfo

public class AiLocModeProcessor @Inject constructor(private val interactions: AiLocInteractions) {
    public fun processOp(npc: Npc, op: InteractionOp) {
        val target = npc.resolveTarget()
        if (target == null) {
            npc.resetMode()
            return
        }
        npc.clearIdleCycles()
        interactions.interactOp(npc, target, op)
    }

    public fun processAp(npc: Npc, op: InteractionOp) {
        val target = npc.resolveTarget()
        if (target == null) {
            npc.resetMode()
            return
        }
        npc.clearIdleCycles()
        interactions.interactAp(npc, target, op)
    }

    private fun Npc.resolveTarget(): BoundLocInfo? {
        val interaction = this.interaction ?: return null
        if (interaction !is InteractionLoc) {
            return null
        }
        return interaction.target
    }
}
