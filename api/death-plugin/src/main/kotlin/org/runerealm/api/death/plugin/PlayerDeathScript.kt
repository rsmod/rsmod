package org.runerealm.api.death.plugin

import jakarta.inject.Inject
import org.runerealm.api.config.refs.queues
import org.runerealm.api.death.PlayerDeath
import org.runerealm.api.script.onPlayerQueue
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class PlayerDeathScript @Inject constructor(private val death: PlayerDeath) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerQueue(queues.death) { death.death(this) }
    }
}
