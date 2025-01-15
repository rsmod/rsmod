package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.player.interact.InvInteractions
import org.rsmod.api.player.ui.IfOverlayButton
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.script.onIfOpen
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InvInteractionOp
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class InvOpScript @Inject private constructor(private val interactions: InvInteractions) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onIfOpen(interfaces.inventory_tab) { player.onInvOpen() }
        onIfOverlayButton(components.inv_inv) { onInvButton() }
    }

    private fun IfOverlayButton.onInvButton() {
        val invOp = this.op.toInvOp() ?: throw IllegalStateException("Op not supported: $this")
        interactions.interact(player, player.inv, comsub, invOp)
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
            IfButtonOp.Op10 -> InvInteractionOp.Op8
        }
}
