package org.rsmod.api.npc.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.npc.events.interact.AiNpcUContentEvents
import org.rsmod.api.npc.events.interact.AiNpcUDefaultEvents
import org.rsmod.api.npc.events.interact.AiNpcUEvents
import org.rsmod.api.npc.events.interact.ApEvent
import org.rsmod.api.npc.events.interact.OpEvent
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.isType
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.UnpackedObjType

public class AiNpcUInteractions
@Inject
private constructor(private val eventBus: EventBus, private val npcTypes: NpcTypeList) {
    private val logger = InlineLogger()

    public suspend fun interactOp(
        access: StandardNpcAccess,
        target: Npc,
        inv: Inventory,
        invSlot: Int,
        npcType: UnpackedNpcType,
        objType: UnpackedObjType,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(obj, objType)) {
            access.opNpcU(target, invSlot, npcType, objType)
        }
    }

    private suspend fun StandardNpcAccess.opNpcU(
        target: Npc,
        invSlot: Int,
        npcType: UnpackedNpcType,
        objType: UnpackedObjType,
    ) {
        val script = opTrigger(target, invSlot, target.visType, objType)
        if (script != null) {
            eventBus.publish(this, script)
            return
        }
        logger.debug {
            "aiOpNpcU for `${objType.name}` on `${npcType.name}` is not implemented: " +
                "npcType=$npcType, objType=$objType"
        }
    }

    private fun opTrigger(
        target: Npc,
        invSlot: Int,
        npcType: UnpackedNpcType,
        objType: UnpackedObjType,
    ): OpEvent? {
        val contentGroup = npcType.contentGroup

        val typeScript = AiNpcUEvents.Op(target, invSlot, objType, npcType)
        if (eventBus.contains(typeScript::class.java, typeScript.id)) {
            return typeScript
        }

        val groupScript = AiNpcUContentEvents.Op(target, invSlot, objType, contentGroup)
        if (eventBus.contains(groupScript::class.java, groupScript.id)) {
            return groupScript
        }

        val defaultTypeScript = AiNpcUDefaultEvents.OpType(target, invSlot, objType, npcType)
        if (eventBus.contains(defaultTypeScript::class.java, defaultTypeScript.id)) {
            return defaultTypeScript
        }

        val defGroupScript = AiNpcUDefaultEvents.OpContent(target, invSlot, objType, contentGroup)
        if (eventBus.contains(defGroupScript::class.java, defGroupScript.id)) {
            return defGroupScript
        }

        return null
    }

    public suspend fun interactAp(
        access: StandardNpcAccess,
        target: Npc,
        inv: Inventory,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(obj, objType)) {
            access.apNpcU(target, invSlot, objType)
        }
    }

    private suspend fun StandardNpcAccess.apNpcU(
        target: Npc,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val script = apTrigger(target, invSlot, target.visType, objType) ?: return
        eventBus.publish(this, script)
    }

    private fun apTrigger(
        target: Npc,
        invSlot: Int,
        npcType: UnpackedNpcType,
        objType: UnpackedObjType,
    ): ApEvent? {
        val contentGroup = npcType.contentGroup

        val typeScript = AiNpcUEvents.Ap(target, invSlot, objType, npcType)
        if (eventBus.contains(typeScript::class.java, typeScript.id)) {
            return typeScript
        }

        val groupScript = AiNpcUContentEvents.Ap(target, invSlot, objType, contentGroup)
        if (eventBus.contains(groupScript::class.java, groupScript.id)) {
            return groupScript
        }

        val defaultTypeScript = AiNpcUDefaultEvents.ApType(target, invSlot, objType, npcType)
        if (eventBus.contains(defaultTypeScript::class.java, defaultTypeScript.id)) {
            return defaultTypeScript
        }

        val defGroupScript = AiNpcUDefaultEvents.ApContent(target, invSlot, objType, contentGroup)
        if (eventBus.contains(defGroupScript::class.java, defGroupScript.id)) {
            return defGroupScript
        }

        return null
    }

    private fun objectVerify(obj: InvObj?, type: UnpackedObjType): Boolean = obj.isType(type)
}
