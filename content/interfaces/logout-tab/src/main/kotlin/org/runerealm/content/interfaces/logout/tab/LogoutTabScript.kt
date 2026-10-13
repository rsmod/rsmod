package org.runerealm.content.interfaces.logout.tab

import jakarta.inject.Inject
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.ui.ifClose
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

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
            if (isBusy2 && mapClock > preventLogoutUntil) {
                playerWalk(coords)
            }
            logOut()
        }
    }
}
