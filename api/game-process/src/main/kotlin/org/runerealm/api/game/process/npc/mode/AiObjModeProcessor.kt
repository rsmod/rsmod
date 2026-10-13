package org.runerealm.api.game.process.npc.mode

import jakarta.inject.Inject
import org.runerealm.api.npc.interact.AiObjInteractions
import org.runerealm.game.entity.Npc
import org.runerealm.game.interact.InteractionObj
import org.runerealm.game.interact.InteractionOp
import org.runerealm.game.obj.Obj

public class AiObjModeProcessor @Inject constructor(private val interactions: AiObjInteractions) {
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

    private fun Npc.resolveTarget(): Obj? {
        val interaction = this.interaction ?: return null
        if (interaction !is InteractionObj) {
            return null
        }
        return interaction.target
    }
}
