package org.rsmod.api.death.plugin

import jakarta.inject.Inject
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.varnbits
import org.rsmod.api.death.NpcDeath
import org.rsmod.api.npc.events.NpcEvents
import org.rsmod.api.npc.vars.boolVarnBit
import org.rsmod.api.script.onEvent
import org.rsmod.api.script.onNpcQueue
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class NpcDeathScript @Inject constructor(private val death: NpcDeath) : PluginScript() {
    private var Npc.pendingRespawn by boolVarnBit(varnbits.respawn_pending)

    override fun ScriptContext.startUp() {
        onNpcQueue(queues.death) { death.deathWithDrops(this) }
        onEvent<NpcEvents.Reveal> { npc.respawn() }
    }

    private fun Npc.respawn() {
        if (!pendingRespawn) {
            return
        }
        pendingRespawn = false
        setRespawnValues()
    }
}
