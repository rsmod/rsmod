package org.rsmod.api.hit.plugin

import org.rsmod.api.player.hit.DeferredPlayerHit
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onPlayerQueueWithArgs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

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
