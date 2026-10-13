package org.runerealm.api.player.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.runerealm.api.config.constants
import org.runerealm.api.player.events.interact.ApEvent
import org.runerealm.api.player.events.interact.OpEvent
import org.runerealm.api.player.events.interact.PlayerUContentEvents
import org.runerealm.api.player.events.interact.PlayerUEvents
import org.runerealm.api.player.output.ChatType
import org.runerealm.api.player.output.UpdateInventory.resendSlot
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.InvObj
import org.runerealm.game.inv.Inventory
import org.runerealm.game.inv.isType
import org.runerealm.game.type.obj.UnpackedObjType

public class PlayerUInteractions @Inject constructor(private val eventBus: EventBus) {
    private val logger = InlineLogger()

    public suspend fun interactOp(
        access: ProtectedAccess,
        target: Player,
        inv: Inventory,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(inv, obj, objType)) {
            access.opPlayerU(target, invSlot, objType)
        }
    }

    private suspend fun ProtectedAccess.opPlayerU(
        target: Player,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val script = opTrigger(target, invSlot, objType)
        if (script != null) {
            eventBus.publish(this, script)
            return
        }
        mes(constants.dm_default, ChatType.Engine)
        logger.debug {
            "opPlayerU for `${objType.name}` is not implemented: " +
                "target='${target.displayName}', objType=$objType"
        }
    }

    private fun opTrigger(target: Player, invSlot: Int, objType: UnpackedObjType): OpEvent? {
        val typeScript = PlayerUEvents.Op(target, invSlot, objType)
        if (eventBus.contains(typeScript::class.java, typeScript.id)) {
            return typeScript
        }

        val contentScript = PlayerUContentEvents.Op(target, invSlot, objType)
        if (eventBus.contains(contentScript::class.java, contentScript.id)) {
            return contentScript
        }

        return null
    }

    public suspend fun interactAp(
        access: ProtectedAccess,
        target: Player,
        inv: Inventory,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(inv, obj, objType)) {
            access.apPlayerU(target, invSlot, objType)
        }
    }

    private suspend fun ProtectedAccess.apPlayerU(
        target: Player,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val script = apTrigger(target, invSlot, objType)
        if (script != null) {
            eventBus.publish(this, script)
            return
        }
        apRange(-1)
    }

    private fun apTrigger(target: Player, invSlot: Int, objType: UnpackedObjType): ApEvent? {
        val typeScript = PlayerUEvents.Ap(target, invSlot, objType)
        if (eventBus.contains(typeScript::class.java, typeScript.id)) {
            return typeScript
        }

        val contentScript = PlayerUContentEvents.Ap(target, invSlot, objType)
        if (eventBus.contains(contentScript::class.java, contentScript.id)) {
            return contentScript
        }

        return null
    }

    private fun objectVerify(inv: Inventory, obj: InvObj?, type: UnpackedObjType): Boolean {
        if (obj == null || !obj.isType(type)) {
            resendSlot(inv, 0)
            return false
        }
        return true
    }
}
