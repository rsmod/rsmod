package org.rsmod.api.combat.scripts

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.npc.combatDefaultRetaliateOp
import org.rsmod.api.config.refs.queues
import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.npc.interact.AiPlayerInteractions
import org.rsmod.api.script.onNpcQueue
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal class NpcRetaliateScript
@Inject
constructor(private val interactions: AiPlayerInteractions) : PluginScript() {
    override fun ScriptContext.startUp() {
        onNpcQueue(queues.com_retaliate_player) { autoRetaliatePlayer() }
    }

    private fun StandardNpcAccess.autoRetaliatePlayer() {
        npc.combatDefaultRetaliateOp(interactions)
    }
}
