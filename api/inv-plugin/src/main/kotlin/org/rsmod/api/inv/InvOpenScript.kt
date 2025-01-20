package org.rsmod.api.inv

import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.script.onIfOpen
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class InvOpenScript : PluginScript() {
    override fun ScriptContext.startUp() {
        onIfOpen(interfaces.inventory_tab) { player.onInvOpen() }
    }

    private fun Player.onInvOpen() {
        ifSetEvents(
            components.inv_inv,
            inv.indices,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op6,
            IfEvent.Op7,
            IfEvent.Op10,
            IfEvent.TgtObj,
            IfEvent.TgtNpc,
            IfEvent.TgtLoc,
            IfEvent.TgtPlayer,
            IfEvent.TgtInv,
            IfEvent.TgtCom,
            IfEvent.Depth1,
            IfEvent.DragTarget,
            IfEvent.Target,
        )
    }
}
