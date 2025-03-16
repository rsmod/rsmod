package org.rsmod.api.game.process.npc.mode

import jakarta.inject.Inject
import org.rsmod.api.npc.interact.AiNpcInteractions
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.interact.InteractionNpc
import org.rsmod.game.interact.InteractionOp

public class AiNpcModeProcessor
@Inject
constructor(private val npcList: NpcList, private val interactions: AiNpcInteractions) {
    public fun process(npc: Npc, op: InteractionOp) {
        val target = npc.resolveTarget()
        if (target == null) {
            npc.resetMode()
            return
        }
        interactions.interact(npc, target, op)
    }

    private fun Npc.resolveTarget(): Npc? {
        val interaction = this.interaction ?: return null
        if (interaction !is InteractionNpc) {
            return null
        }
        return interaction.uid.resolve(npcList)
    }
}
