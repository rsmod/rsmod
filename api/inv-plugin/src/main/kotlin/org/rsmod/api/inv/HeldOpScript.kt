package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.objs
import org.rsmod.api.player.interact.HeldInteractions
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.ui.IfButtonDrag
import org.rsmod.api.player.ui.IfOverlayButton
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.script.advanced.onIfButtonDrag
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.HeldOp
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.isType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class HeldOpScript
@Inject
private constructor(
    private val eventBus: EventBus,
    private val interactions: HeldInteractions,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    /*
     * Note: Held interactions used to get protected access through specialized packets (OpHeld).
     * However, this was changed around revision 204. They are now given protected-access later on.
     * (In this script)
     */

    override fun ScriptContext.startUp() {
        onIfOverlayButton(components.inv_inv) { opHeldButton() }
        onIfButtonDrag(components.inv_inv, components.inv_inv) { dragHeldButton() }
    }

    private fun Player.opHeld(invSlot: Int, op: HeldOp) {
        clearPendingAction(eventBus)
        resetFaceEntity()
        if (isAccessProtected) {
            resendSlot(this, inv, 0)
            return
        }
        protectedAccess.launch(this) { interactions.interact(this, inv, invSlot, op) }
    }

    private fun Player.dragHeld(
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

    private fun IfOverlayButton.opHeldButton() {
        if (op == IfButtonOp.Op10) {
            interactions.examine(player, player.inv, comsub)
            return
        }
        val heldOp = op.toHeldOp() ?: throw IllegalStateException("Op not supported: $this")
        player.opHeld(comsub, heldOp)
    }

    private fun IfButtonDrag.dragHeldButton() {
        val fromSlot = selectedSlot ?: return
        val intoSlot = targetSlot ?: return
        val selectedObj = selectedObj.convertNullReplacement()
        val targetObj = targetObj.convertNullReplacement()
        player.dragHeld(fromSlot, intoSlot, selectedObj, targetObj)
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

    private fun IfButtonOp.toHeldOp(): HeldOp? =
        when (this) {
            IfButtonOp.Op1 -> null
            IfButtonOp.Op2 -> HeldOp.Op1
            IfButtonOp.Op3 -> HeldOp.Op2
            IfButtonOp.Op4 -> HeldOp.Op3
            IfButtonOp.Op5 -> null
            IfButtonOp.Op6 -> HeldOp.Op4
            IfButtonOp.Op7 -> HeldOp.Op5
            IfButtonOp.Op8 -> null
            IfButtonOp.Op9 -> null
            IfButtonOp.Op10 -> null
        }
}
