package org.runerealm.api.inv

import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.player.ui.ifSetEvents
import org.runerealm.api.script.onIfOpen
import org.runerealm.game.entity.Player
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class InvOpenScript : PluginScript() {
    override fun ScriptContext.startup() {
        onIfOpen(interfaces.inventory) { player.onInvOpen() }
    }

    private fun Player.onInvOpen() {
        ifSetEvents(
            components.inv_items,
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
