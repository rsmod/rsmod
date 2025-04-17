package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.player.events.interact.LocTDefaultEvents
import org.rsmod.api.player.interact.LocUInteractions
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLocT
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class LocUOpScript
@Inject
private constructor(private val interactions: LocUInteractions, private val objTypes: ObjTypeList) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLocT(components.inv_items) { opLocU(it) }
    }

    private suspend fun ProtectedAccess.opLocU(op: LocTDefaultEvents.Op) {
        val objType = op.objType?.let(objTypes::get) ?: return resendSlot(inv, 0)
        interactions.interactOp(this, op.loc, op.base, op.type, objType, inv, op.comsub)
    }
}
