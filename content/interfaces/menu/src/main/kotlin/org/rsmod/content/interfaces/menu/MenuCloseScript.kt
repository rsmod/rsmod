package org.rsmod.content.interfaces.menu

import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.output.ClientScripts
import org.rsmod.api.script.onIfClose
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class MenuCloseScript : PluginScript() {
    override fun ScriptContext.startup() {
        onIfClose(interfaces.menu) { player.onMenuClose() }
    }

    private fun Player.onMenuClose() {
        // TODO: It seems there is a state for whether the player has their input blocked off.
        // This can be seen by resuming/selecting a choice in `menu`. This cs2 is usually called
        // _after_ the menu interface is closed. However, when resuming, it will call this cs2
        // _before_ the interface closes and once the interface closes, it will not call
        // `chatdefault_restoreinput` a second time.
        ClientScripts.chatDefaultRestoreInput(this)
    }
}
