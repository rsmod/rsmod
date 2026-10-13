package org.runerealm.api.inv

import jakarta.inject.Inject
import org.runerealm.api.config.refs.components
import org.runerealm.api.player.events.interact.NpcTDefaultEvents
import org.runerealm.api.player.interact.NpcUInteractions
import org.runerealm.api.player.output.UpdateInventory.resendSlot
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpcT
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class NpcUOpScript
@Inject
private constructor(private val interactions: NpcUInteractions, private val objTypes: ObjTypeList) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpcT(components.inv_items) { opNpcU(it) }
    }

    private suspend fun ProtectedAccess.opNpcU(op: NpcTDefaultEvents.Op) {
        val objType = op.objType?.let(objTypes::get) ?: return resendSlot(inv, 0)
        interactions.interactOp(this, op.npc, inv, op.comsub, op.npcType, objType)
    }
}
