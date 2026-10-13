package org.runerealm.api.hit.plugin

import org.runerealm.api.script.onNpcQueueWithArgs
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class NpcHitScript : PluginScript() {
    override fun ScriptContext.startup() {
        onNpcQueueWithArgs(hit_queues.standard) { processQueuedHit(it.args) }
    }
}
