package org.runerealm.api.inv

import jakarta.inject.Inject
import org.runerealm.api.config.refs.components
import org.runerealm.api.player.interact.HeldInteractions
import org.runerealm.api.player.output.UpdateInventory.resendSlot
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.protect.clearPendingAction
import org.runerealm.api.player.ui.IfOverlayButton
import org.runerealm.api.player.ui.IfOverlayDrag
import org.runerealm.api.player.ui.IfOverlaySubOp
import org.runerealm.api.player.ui.ifClose
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.script.onIfOverlayDrag
import org.runerealm.api.script.onIfOverlaySubOp
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.interact.HeldOp
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class HeldOpScript
@Inject
private constructor(
    private val eventBus: EventBus,
    private val interactions: HeldInteractions,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onIfOverlayButton(components.inv_items) { opHeldButton() }
        onIfOverlaySubOp(components.inv_items) { opHeldSubOp() }
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

    private fun IfOverlaySubOp.opHeldSubOp() {
        val heldOp = op.toHeldOp() ?: return
        val invSlot = comsub
        player.ifClose(eventBus)
        if (player.isAccessProtected) {
            resendSlot(player.inv, 0)
            return
        }
        protectedAccess.launch(player) {
            clearPendingAction()
            interactions.interactSubOp(this, inv, invSlot, heldOp, subop)
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
