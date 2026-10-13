package org.runerealm.api.npc.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.runerealm.api.npc.access.StandardNpcAccess
import org.runerealm.api.npc.events.interact.AiPlayerUContentEvents
import org.runerealm.api.npc.events.interact.AiPlayerUEvents
import org.runerealm.api.npc.events.interact.ApEvent
import org.runerealm.api.npc.events.interact.OpEvent
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.InvObj
import org.runerealm.game.inv.Inventory
import org.runerealm.game.inv.isType
import org.runerealm.game.type.obj.UnpackedObjType

public class AiPlayerUInteractions @Inject constructor(private val eventBus: EventBus) {
    private val logger = InlineLogger()

    public suspend fun interactOp(
        access: StandardNpcAccess,
        target: Player,
        inv: Inventory,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(obj, objType)) {
            access.opPlayerU(target, invSlot, objType)
        }
    }

    private suspend fun StandardNpcAccess.opPlayerU(
        target: Player,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val script = opTrigger(target, invSlot, objType)
        if (script != null) {
            eventBus.publish(this, script)
            return
        }
        logger.debug {
            "aiOpPlayerU for `${objType.name}` is not implemented: " +
                "target=${target.displayName}, npcType=${npc.visType}, objType=$objType"
        }
    }

    private fun StandardNpcAccess.opTrigger(
        target: Player,
        invSlot: Int,
        objType: UnpackedObjType,
    ): OpEvent? {
        val typeScript = AiPlayerUEvents.Op(target, invSlot, objType)
        if (eventBus.contains(typeScript::class.java, typeScript.id)) {
            return typeScript
        }

        val contentScript =
            AiPlayerUContentEvents.Op(target, invSlot, objType, npc.visType.contentGroup)
        if (eventBus.contains(contentScript::class.java, contentScript.id)) {
            return contentScript
        }

        return null
    }

    public suspend fun interactAp(
        access: StandardNpcAccess,
        target: Player,
        inv: Inventory,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(obj, objType)) {
            access.apPlayerU(target, invSlot, objType)
        }
    }

    private suspend fun StandardNpcAccess.apPlayerU(
        target: Player,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val script = apTrigger(target, invSlot, objType) ?: return
        eventBus.publish(this, script)
    }

    private fun StandardNpcAccess.apTrigger(
        target: Player,
        invSlot: Int,
        objType: UnpackedObjType,
    ): ApEvent? {
        val typeScript = AiPlayerUEvents.Ap(target, invSlot, objType)
        if (eventBus.contains(typeScript::class.java, typeScript.id)) {
            return typeScript
        }

        val contentScript =
            AiPlayerUContentEvents.Ap(target, invSlot, objType, npc.visType.contentGroup)
        if (eventBus.contains(contentScript::class.java, contentScript.id)) {
            return contentScript
        }

        return null
    }

    private fun objectVerify(obj: InvObj?, type: UnpackedObjType): Boolean = obj.isType(type)
}
