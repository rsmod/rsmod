package org.runerealm.api.game.process.npc.mode

import jakarta.inject.Inject
import org.runerealm.api.npc.interact.AiNpcInteractions
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.NpcList
import org.runerealm.game.interact.InteractionNpc
import org.runerealm.game.interact.InteractionOp

public class AiNpcModeProcessor
@Inject
constructor(private val npcList: NpcList, private val interactions: AiNpcInteractions) {
    public fun processOp(npc: Npc, op: InteractionOp) {
        val target = npc.resolveTarget()
        if (target == null) {
            npc.resetMode()
            return
        }
        npc.clearIdleCycles()
        npc.faceNpc(target)
        interactions.interactOp(npc, target, op)
    }

    public fun processAp(npc: Npc, op: InteractionOp) {
        val target = npc.resolveTarget()
        if (target == null) {
            npc.resetMode()
            return
        }
        npc.clearIdleCycles()
        npc.faceNpc(target)
        interactions.interactAp(npc, target, op)
    }

    private fun Npc.resolveTarget(): Npc? {
        val interaction = this.interaction ?: return null
        if (interaction !is InteractionNpc) {
            return null
        }
        return interaction.uid.resolve(npcList)
    }
}
