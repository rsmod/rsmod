package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.player.interact.HeldInteractions
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.ui.IfOverlayButton
import org.rsmod.api.player.ui.IfOverlayDrag
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.api.script.onIfOverlayDrag
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.HeldOp
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class HeldOpScript
@Inject
private constructor(
    private val eventBus: EventBus,
    private val interactions: HeldInteractions,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onIfOverlayButton(components.inv_items) { opHeldButton() }
        onIfOverlayDrag(components.inv_items) { dragHeldButton() }
    }

    private fun IfOverlayButton.opHeldButton() {
        if (op == IfButtonOp.Op10) {
            interactions.examine(player, player.inv, comsub)
            return
        }
        val heldOp = op.toHeldOp() ?: throw IllegalStateException("Op not supported: $this")
        player.opHeld(comsub, heldOp)
    }

    private fun Player.opHeld(invSlot: Int, op: HeldOp) {
        ifClose(eventBus)
        if (isAccessProtected) {
            resendSlot(inv, 0)
            return
        }
        protectedAccess.launch(this) {
            clearPendingAction()
            interactions.interact(this, inv, invSlot, op)
        }
    }

    private fun IfOverlayDrag.dragHeldButton() {
        val fromSlot = selectedSlot ?: return
        val intoSlot = targetSlot ?: return
        player.dragHeld(fromSlot, intoSlot)
    }

    private fun Player.dragHeld(fromSlot: Int, intoSlot: Int) {
        ifClose(eventBus)
        if (isAccessProtected) {
            resendSlot(inv, 0)
            return
        }
        protectedAccess.launch(this) { invMoveToSlot(inv, inv, fromSlot, intoSlot) }
    }

    private fun IfButtonOp.toHeldOp(): HeldOp? =
        when (this) {
            IfButtonOp.Op2 -> HeldOp.Op1
            IfButtonOp.Op3 -> HeldOp.Op2
            IfButtonOp.Op4 -> HeldOp.Op3
            IfButtonOp.Op6 -> HeldOp.Op4
            IfButtonOp.Op7 -> HeldOp.Op5
            else -> null
        }
}
