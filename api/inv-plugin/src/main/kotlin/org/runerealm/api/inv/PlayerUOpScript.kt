package org.runerealm.api.inv

import jakarta.inject.Inject
import org.runerealm.api.config.refs.components
import org.runerealm.api.player.events.interact.PlayerTEvents
import org.runerealm.api.player.interact.PlayerUInteractions
import org.runerealm.api.player.output.UpdateInventory.resendSlot
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpPlayerT
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class PlayerUOpScript
@Inject
constructor(private val interactions: PlayerUInteractions, private val objTypes: ObjTypeList) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onOpPlayerT(components.inv_items) { opPlayerU(it) }
    }

    private suspend fun ProtectedAccess.opPlayerU(op: PlayerTEvents.Op) {
        val objType = op.objType?.let(objTypes::get) ?: return resendSlot(inv, 0)
        interactions.interactOp(this, op.target, inv, op.comsub, objType)
    }
}
