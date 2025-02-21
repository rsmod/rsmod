package org.rsmod.content.interfaces.fade.overlay

import jakarta.inject.Inject
import org.rsmod.api.config.refs.queues
import org.rsmod.api.player.cinematic.Cinematic
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onPlayerQueue
import org.rsmod.events.EventBus
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class FadeOverlayScript @Inject constructor(private val eventBus: EventBus) : PluginScript() {
    override fun ScriptContext.startUp() {
        onPlayerQueue(queues.fade_overlay_close) { onCloseQueue() }
    }

    private fun ProtectedAccess.onCloseQueue() {
        Cinematic.closeFadeOverlay(player, eventBus)
    }
}
