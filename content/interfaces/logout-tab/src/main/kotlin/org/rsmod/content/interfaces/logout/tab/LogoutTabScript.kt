package org.rsmod.content.interfaces.logout.tab

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class LogoutTabScript
@Inject
constructor(private val eventBus: EventBus, private val protectedAccess: ProtectedAccessLauncher) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onIfOverlayButton(logout_components.logout) { player.requestLogout() }
    }

    private fun Player.requestLogout() {
        ifClose(eventBus)
        protectedAccess.launch(this) {
            if (isBusy2) {
                playerWalk(coords)
            }
            logOut()
        }
    }
}
