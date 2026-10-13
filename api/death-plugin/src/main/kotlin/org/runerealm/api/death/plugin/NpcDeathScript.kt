package org.runerealm.api.death.plugin

import jakarta.inject.Inject
import org.runerealm.api.config.refs.queues
import org.runerealm.api.death.NpcDeath
import org.runerealm.api.script.onEvent
import org.runerealm.api.script.onNpcQueue
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.npc.NpcStateEvents
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class NpcDeathScript @Inject constructor(private val death: NpcDeath) : PluginScript() {
    override fun ScriptContext.startup() {
        onNpcQueue(queues.death) { death.deathWithDrops(this) }
        onEvent<NpcStateEvents.Respawn> { npc.respawn() }
    }

    private fun Npc.respawn() {
        setRespawnValues()
    }
}
