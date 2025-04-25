package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.player.events.interact.NpcTDefaultEvents
import org.rsmod.api.player.interact.NpcUInteractions
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpcT
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

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
