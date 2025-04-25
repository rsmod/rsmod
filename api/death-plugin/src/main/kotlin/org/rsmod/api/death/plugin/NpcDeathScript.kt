package org.rsmod.api.death.plugin

import jakarta.inject.Inject
import org.rsmod.api.config.refs.queues
import org.rsmod.api.death.NpcDeath
import org.rsmod.api.script.onEvent
import org.rsmod.api.script.onNpcQueue
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.npc.NpcStateEvents
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class NpcDeathScript @Inject constructor(private val death: NpcDeath) : PluginScript() {
    override fun ScriptContext.startup() {
        onNpcQueue(queues.death) { death.deathWithDrops(this) }
        onEvent<NpcStateEvents.Respawn> { npc.respawn() }
    }

    private fun Npc.respawn() {
        setRespawnValues()
    }
}
