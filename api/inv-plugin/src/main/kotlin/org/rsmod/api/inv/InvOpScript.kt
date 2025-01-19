package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.objs
import org.rsmod.api.player.interact.InvInteractions
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.ui.IfButtonDrag
import org.rsmod.api.player.ui.IfOverlayButton
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.script.advanced.onIfButtonDrag
import org.rsmod.api.script.onIfOpen
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InvInteractionOp
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.isType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class InvOpScript
@Inject
private constructor(
    private val eventBus: EventBus,
    private val interactions: InvInteractions,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    /*
     * Note: Inv interactions used to get protected access through specialized packets (OpHeld).
     * However, this was changed around revision 204. They are now given protected-access later on.
     * (In this script)
     */

    override fun ScriptContext.startUp() {
        onIfOpen(interfaces.inventory_tab) { player.onInvOpen() }
        onIfOverlayButton(components.inv_inv) { opInvObjButton() }
        onIfButtonDrag(components.inv_inv, components.inv_inv) { dragInvObjButton() }
    }

    private fun Player.opInvObj(invSlot: Int, invOp: InvInteractionOp) {
        clearPendingAction(eventBus)
        resetFaceEntity()
        if (isAccessProtected) {
            resendSlot(this, inv, 0)
            return
        }
        protectedAccess.launch(this) { interactions.interact(this, inv, invSlot, invOp) }
    }

    private fun Player.dragInvObj(
        fromSlot: Int,
        intoSlot: Int,
        selectedObj: UnpackedObjType?,
        targetObj: UnpackedObjType?,
    ) {
        ifClose(eventBus)
        if (isAccessProtected) {
            resendSlot(this, inv, 0)
            return
        }
        protectedAccess.launch(this) {
            interactions.drag(player, player.inv, fromSlot, intoSlot, selectedObj, targetObj)
        }
    }

    private fun IfOverlayButton.opInvObjButton() {
        if (op == IfButtonOp.Op10) {
            interactions.examine(player, player.inv, comsub)
            return
        }
        val invOp = op.toInvOp() ?: throw IllegalStateException("Op not supported: $this")
        player.opInvObj(comsub, invOp)
    }

    private fun IfButtonDrag.dragInvObjButton() {
        val fromSlot = selectedSlot ?: return
        val intoSlot = targetSlot ?: return
        val selectedObj = selectedObj.convertNullReplacement()
        val targetObj = targetObj.convertNullReplacement()
        player.dragInvObj(fromSlot, intoSlot, selectedObj, targetObj)
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

    private fun IfButtonOp.toInvOp(): InvInteractionOp? =
        when (this) {
            IfButtonOp.Op1 -> null
            IfButtonOp.Op2 -> InvInteractionOp.Op1
            IfButtonOp.Op3 -> InvInteractionOp.Op2
            IfButtonOp.Op4 -> InvInteractionOp.Op3
            IfButtonOp.Op5 -> null
            IfButtonOp.Op6 -> InvInteractionOp.Op4
            IfButtonOp.Op7 -> InvInteractionOp.Op5
            IfButtonOp.Op8 -> InvInteractionOp.Op6
            IfButtonOp.Op9 -> InvInteractionOp.Op7
            IfButtonOp.Op10 -> null
        }

    // Client replaces empty obj ids with `6512`. To make life easier, we simply replace those
    // with null obj types as that's what the server expects.
    private fun UnpackedObjType?.convertNullReplacement(): UnpackedObjType? {
        return if (isType(objs.null_item_placeholder)) {
            null
        } else {
            this
        }
    }
}
