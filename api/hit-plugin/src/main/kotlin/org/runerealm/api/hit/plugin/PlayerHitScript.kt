package org.runerealm.api.hit.plugin

import org.runerealm.api.player.hit.DeferredPlayerHit
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onPlayerQueueWithArgs
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class PlayerHitScript : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerQueueWithArgs(hit_queues.standard) { processQueuedHit(it.args) }
        onPlayerQueueWithArgs(hit_queues.impact) { processQueuedDeferredHit(it.args) }
    }

    private fun ProtectedAccess.processQueuedDeferredHit(deferred: DeferredPlayerHit) {
        val (builder, modifier) = deferred
        processQueuedHit(builder, modifier)
    }
}
