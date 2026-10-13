package org.runerealm.api.player.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.runerealm.api.config.constants
import org.runerealm.api.player.events.interact.HeldUContentEvents
import org.runerealm.api.player.events.interact.HeldUDefaultEvents
import org.runerealm.api.player.events.interact.HeldUEvents
import org.runerealm.api.player.output.ChatType
import org.runerealm.api.player.output.UpdateInventory.resendSlot
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.events.EventBus
import org.runerealm.events.SuspendEvent
import org.runerealm.game.inv.InvObj
import org.runerealm.game.inv.Inventory
import org.runerealm.game.inv.isType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.obj.UnpackedObjType

public class HeldUInteractions
@Inject
constructor(private val eventBus: EventBus, private val objTypes: ObjTypeList) {
    private val logger = InlineLogger()

    public suspend fun interact(
        access: ProtectedAccess,
        inventory: Inventory,
        selectedObjType: UnpackedObjType,
        selectedSlot: Int,
        targetObjType: UnpackedObjType,
        targetSlot: Int,
    ) {
        if (selectedSlot == targetSlot) {
            resendSlot(inventory, 0)
            return
        }

        val selectedObj = inventory[selectedSlot]
        if (!objectVerify(inventory, selectedObj, selectedObjType)) {
            return
        }

        val targetObj = inventory[targetSlot]
        if (!objectVerify(inventory, targetObj, targetObjType)) {
            return
        }

        access.opHeldU(selectedObjType, selectedSlot, targetObjType, targetSlot)
    }

    private suspend fun ProtectedAccess.opHeldU(
        selectedObjType: UnpackedObjType,
        selectedSlot: Int,
        targetObjType: UnpackedObjType,
        targetSlot: Int,
    ) {
        val firstCombination = opTrigger(selectedObjType, selectedSlot, targetObjType, targetSlot)
        if (firstCombination != null) {
            eventBus.publish(this, firstCombination)
            return
        }

        val secondCombination = opTrigger(targetObjType, targetSlot, selectedObjType, selectedSlot)
        if (secondCombination != null) {
            eventBus.publish(this, secondCombination)
            return
        }

        mes(constants.dm_default, ChatType.Engine)
        logger.debug {
            "opHeldU for `${selectedObjType.name}` on `${targetObjType.name}` is not " +
                "implemented: selected=$selectedObjType, target=$targetObjType"
        }
    }

    private fun opTrigger(
        first: UnpackedObjType,
        firstSlot: Int,
        second: UnpackedObjType,
        secondSlot: Int,
    ): SuspendEvent<ProtectedAccess>? {
        val typeScript = HeldUEvents.Type(first, firstSlot, second, secondSlot)
        if (eventBus.contains(typeScript::class.java, typeScript.id)) {
            return typeScript
        }

        val contentTypeScript = HeldUContentEvents.Type(first, firstSlot, second, secondSlot)
        if (eventBus.contains(contentTypeScript::class.java, contentTypeScript.id)) {
            return contentTypeScript
        }

        val contentScript = HeldUContentEvents.Content(first, firstSlot, second, secondSlot)
        if (eventBus.contains(contentScript::class.java, contentScript.id)) {
            return contentScript
        }

        val defaultTypeScript = HeldUDefaultEvents.Type(first, firstSlot, second, secondSlot)
        if (eventBus.contains(defaultTypeScript::class.java, defaultTypeScript.id)) {
            return defaultTypeScript
        }

        val defaultContentScript = HeldUDefaultEvents.Content(first, firstSlot, second, secondSlot)
        if (eventBus.contains(defaultContentScript::class.java, defaultContentScript.id)) {
            return defaultContentScript
        }

        return null
    }

    private fun objectVerify(inv: Inventory, obj: InvObj?, type: ObjType): Boolean {
        if (obj == null || !obj.isType(type)) {
            resendSlot(inv, 0)
            return false
        }
        return true
    }
}
