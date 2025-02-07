package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.player.events.interact.NpcTDefaultEvents
import org.rsmod.api.player.interact.HeldUInteractions
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onApNpcT
import org.rsmod.api.script.onOpNpcT
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class HeldUOpScript
@Inject
private constructor(
    private val interactions: HeldUInteractions,
    private val objTypes: ObjTypeList,
) : PluginScript() {
    override fun ScriptContext.startUp() {
        onApNpcT(components.inv_inv) { apHeldU(it) }
        onOpNpcT(components.inv_inv) { opHeldU(it) }
    }

    private suspend fun ProtectedAccess.apHeldU(ap: NpcTDefaultEvents.Ap) {
        val objType = ap.objType?.let(objTypes::get) ?: return resendSlot(inv, 0)
        interactions.interactAp(this, ap.npc, inv, ap.comsub, objType)
    }

    private suspend fun ProtectedAccess.opHeldU(op: NpcTDefaultEvents.Op) {
        val objType = op.objType?.let(objTypes::get) ?: return resendSlot(inv, 0)
        interactions.interactOp(this, op.npc, inv, op.comsub, op.npcType, objType)
    }
}
