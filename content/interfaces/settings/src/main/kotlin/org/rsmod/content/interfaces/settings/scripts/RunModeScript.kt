package org.rsmod.content.interfaces.settings.scripts

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.api.script.onPlayerQueue
import org.rsmod.content.interfaces.settings.configs.setting_components
import org.rsmod.content.interfaces.settings.configs.setting_queues
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class RunModeScript
@Inject
constructor(private val eventBus: EventBus, private val protectedAccess: ProtectedAccessLauncher) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onIfOverlayButton(setting_components.runbutton_orb) { player.selectRunToggle() }
        onIfOverlayButton(setting_components.runmode) { player.selectRunToggle() }
        onPlayerQueue(setting_queues.runmode_toggle) { toggleRun() }
    }

    private fun Player.selectRunToggle() {
        if (setting_queues.runmode_toggle in queueList) {
            return
        }
        ifClose(eventBus)
        val toggled = protectedAccess.launch(this) { toggleRun() }
        if (!toggled) {
            strongQueue(setting_queues.runmode_toggle, 1)
        }
    }
}
