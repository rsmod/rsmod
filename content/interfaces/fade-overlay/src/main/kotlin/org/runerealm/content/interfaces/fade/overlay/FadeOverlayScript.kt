package org.runerealm.content.interfaces.fade.overlay

import jakarta.inject.Inject
import org.runerealm.api.config.refs.queues
import org.runerealm.api.player.cinematic.Cinematic
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onPlayerQueue
import org.runerealm.events.EventBus
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class FadeOverlayScript @Inject constructor(private val eventBus: EventBus) : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerQueue(queues.fade_overlay_close) { onCloseQueue() }
    }

    private fun ProtectedAccess.onCloseQueue() {
        Cinematic.closeFadeOverlay(player, eventBus)
    }
}
