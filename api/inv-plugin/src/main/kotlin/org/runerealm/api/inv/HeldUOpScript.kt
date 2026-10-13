package org.runerealm.api.inv

import jakarta.inject.Inject
import org.runerealm.api.config.refs.components
import org.runerealm.api.player.interact.HeldUInteractions
import org.runerealm.api.player.output.UpdateInventory.resendSlot
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.protect.clearPendingAction
import org.runerealm.api.player.ui.IfOverlayButtonT
import org.runerealm.api.script.onIfOverlayButtonT
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.type.obj.UnpackedObjType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class HeldUOpScript
@Inject
constructor(
    private val eventBus: EventBus,
    private val interactions: HeldUInteractions,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onIfOverlayButtonT(components.inv_items) { opHeldU() }
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
