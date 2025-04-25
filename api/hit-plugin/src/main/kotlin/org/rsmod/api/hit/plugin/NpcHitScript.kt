package org.rsmod.api.hit.plugin

import org.rsmod.api.script.onNpcQueueWithArgs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class NpcHitScript : PluginScript() {
    override fun ScriptContext.startup() {
        onNpcQueueWithArgs(hit_queues.standard) { processQueuedHit(it.args) }
    }
}
