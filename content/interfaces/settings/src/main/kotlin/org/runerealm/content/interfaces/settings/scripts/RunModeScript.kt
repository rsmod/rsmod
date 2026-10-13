package org.runerealm.content.interfaces.settings.scripts

import jakarta.inject.Inject
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.ui.ifClose
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.script.onPlayerQueue
import org.runerealm.content.interfaces.settings.configs.setting_components
import org.runerealm.content.interfaces.settings.configs.setting_queues
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

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
