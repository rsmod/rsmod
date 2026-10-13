package org.runerealm.api.combat.scripts

import jakarta.inject.Inject
import org.runerealm.api.combat.commons.npc.combatDefaultRetaliateOp
import org.runerealm.api.config.refs.queues
import org.runerealm.api.npc.access.StandardNpcAccess
import org.runerealm.api.npc.interact.AiPlayerInteractions
import org.runerealm.api.script.onNpcQueue
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

internal class NpcRetaliateScript
@Inject
constructor(private val interactions: AiPlayerInteractions) : PluginScript() {
    override fun ScriptContext.startup() {
        onNpcQueue(queues.com_retaliate_player) { autoRetaliatePlayer() }
    }

    private fun StandardNpcAccess.autoRetaliatePlayer() {
        npc.combatDefaultRetaliateOp(interactions)
    }
}
