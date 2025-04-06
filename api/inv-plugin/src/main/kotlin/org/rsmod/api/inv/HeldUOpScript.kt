package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.components
import org.rsmod.api.player.interact.HeldUInteractions
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.ui.IfOverlayButtonT
import org.rsmod.api.script.onIfOverlayButtonT
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class HeldUOpScript
@Inject
constructor(
    private val eventBus: EventBus,
    private val interactions: HeldUInteractions,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    override fun ScriptContext.startUp() {
        onIfOverlayButtonT(components.inv_inv) { opHeldU() }
    }

    private fun IfOverlayButtonT.opHeldU() {
        val selectedObj = this.selectedObj ?: return resendSlot(player.inv, 0)
        val targetObj = this.targetObj ?: return resendSlot(player.inv, 0)
        player.opHeldU(selectedObj, selectedSlot, targetObj, targetSlot)
    }

    private fun Player.opHeldU(
        selectedObj: UnpackedObjType,
        selectedSlot: Int,
        targetObj: UnpackedObjType,
        targetSlot: Int,
    ) {
        clearPendingAction(eventBus)
        resetFaceEntity()
        if (isAccessProtected) {
            resendSlot(inv, 0)
            return
        }
        protectedAccess.launch(this) {
            interactions.interact(
                access = this,
                inventory = inv,
                selectedObjType = selectedObj,
                selectedSlot = selectedSlot,
                targetObjType = targetObj,
                targetSlot = targetSlot,
            )
        }
    }
}
