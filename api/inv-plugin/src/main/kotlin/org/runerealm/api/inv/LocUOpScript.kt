package org.runerealm.api.inv

import jakarta.inject.Inject
import org.runerealm.api.config.refs.components
import org.runerealm.api.player.events.interact.LocTDefaultEvents
import org.runerealm.api.player.interact.LocUInteractions
import org.runerealm.api.player.output.UpdateInventory.resendSlot
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpLocT
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class LocUOpScript
@Inject
private constructor(private val interactions: LocUInteractions, private val objTypes: ObjTypeList) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onOpLocT(components.inv_items) { opLocU(it) }
    }

    private suspend fun ProtectedAccess.opLocU(op: LocTDefaultEvents.Op) {
        val objType = op.objType?.let(objTypes::get) ?: return resendSlot(inv, 0)
        interactions.interactOp(this, op.vis, op.loc, op.type, objType, inv, op.comsub)
    }
}
