package org.runerealm.api.npc.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.runerealm.api.npc.access.StandardNpcAccess
import org.runerealm.api.npc.events.interact.AiLocUContentEvents
import org.runerealm.api.npc.events.interact.AiLocUDefaultEvents
import org.runerealm.api.npc.events.interact.AiLocUEvents
import org.runerealm.api.npc.events.interact.ApEvent
import org.runerealm.api.npc.events.interact.OpEvent
import org.runerealm.events.EventBus
import org.runerealm.game.inv.InvObj
import org.runerealm.game.inv.Inventory
import org.runerealm.game.inv.isType
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.obj.UnpackedObjType

public class AiLocUInteractions
@Inject
private constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val locTypes: LocTypeList,
) {
    private val logger = InlineLogger()

    public suspend fun interactOp(
        access: StandardNpcAccess,
        target: BoundLocInfo,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
        inv: Inventory,
        invSlot: Int,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(obj, objType)) {
            access.opLocU(target, invSlot, locType, objType)
        }
    }

    private suspend fun StandardNpcAccess.opLocU(
        target: BoundLocInfo,
        invSlot: Int,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
    ) {
        val script = opTrigger(target, locType, objType, invSlot)
        if (script != null) {
            eventBus.publish(this, script)
            return
        }
        logger.debug {
            "aiOpLocU for `${objType.name}` on `${locType.name}` is not implemented: " +
                "locType=$locType, objType=$objType"
        }
    }

    public fun opTrigger(
        target: BoundLocInfo,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
        invSlot: Int,
    ): OpEvent? {
        val typeEvent = AiLocUEvents.Op(target, locType, objType, invSlot)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val typeContentEvent = AiLocUContentEvents.OpType(target, locType, objType, invSlot)
        if (eventBus.contains(typeContentEvent::class.java, typeContentEvent.id)) {
            return typeContentEvent
        }

        val defaultTypeScript = AiLocUDefaultEvents.OpType(target, locType, objType, invSlot)
        if (eventBus.contains(defaultTypeScript::class.java, defaultTypeScript.id)) {
            return defaultTypeScript
        }

        val objContentEvent = AiLocUContentEvents.OpContent(target, locType, objType, invSlot)
        if (eventBus.contains(objContentEvent::class.java, objContentEvent.id)) {
            return objContentEvent
        }

        val defGroupScript = AiLocUDefaultEvents.OpContent(target, locType, objType, invSlot)
        if (eventBus.contains(defGroupScript::class.java, defGroupScript.id)) {
            return defGroupScript
        }

        return null
    }

    public suspend fun interactAp(
        access: StandardNpcAccess,
        target: BoundLocInfo,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
        inv: Inventory,
        invSlot: Int,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(obj, objType)) {
            access.apLocU(target, locType, objType, invSlot)
        }
    }

    private suspend fun StandardNpcAccess.apLocU(
        target: BoundLocInfo,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
        invSlot: Int,
    ) {
        val script = apTrigger(target, locType, objType, invSlot) ?: return
        eventBus.publish(this, script)
    }

    private fun apTrigger(
        target: BoundLocInfo,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
        invSlot: Int,
    ): ApEvent? {
        val typeEvent = AiLocUEvents.Ap(target, locType, objType, invSlot)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val locContentEvent = AiLocUContentEvents.ApType(target, locType, objType, invSlot)
        if (eventBus.contains(locContentEvent::class.java, locContentEvent.id)) {
            return locContentEvent
        }

        val defaultTypeScript = AiLocUDefaultEvents.ApType(target, locType, objType, invSlot)
        if (eventBus.contains(defaultTypeScript::class.java, defaultTypeScript.id)) {
            return defaultTypeScript
        }

        val objContentEvent = AiLocUContentEvents.ApContent(target, locType, objType, invSlot)
        if (eventBus.contains(objContentEvent::class.java, objContentEvent.id)) {
            return objContentEvent
        }

        val defGroupScript = AiLocUDefaultEvents.ApContent(target, locType, objType, invSlot)
        if (eventBus.contains(defGroupScript::class.java, defGroupScript.id)) {
            return defGroupScript
        }

        return null
    }

    private fun objectVerify(obj: InvObj?, type: UnpackedObjType): Boolean = obj.isType(type)
}
