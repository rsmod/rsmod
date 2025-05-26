package org.rsmod.api.player.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.config.constants
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.LocUContentEvents
import org.rsmod.api.player.events.interact.LocUDefaultEvents
import org.rsmod.api.player.events.interact.LocUEvents
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.EventBus
import org.rsmod.game.inv.InvObj
import org.rsmod.game.inv.Inventory
import org.rsmod.game.inv.isType
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocEntity
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.varbit.VarBitTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.game.vars.VarPlayerIntMap
import org.rsmod.utils.bits.getBits

public class LocUInteractions
@Inject
private constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val locTypes: LocTypeList,
    private val varpTypes: VarpTypeList,
    private val varBitTypes: VarBitTypeList,
) {
    private val logger = InlineLogger()

    public suspend fun interactOp(
        access: ProtectedAccess,
        target: BoundLocInfo,
        base: BoundLocInfo,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
        inv: Inventory,
        invSlot: Int,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(inv, obj, objType)) {
            access.opLocU(target, base, invSlot, locType, objType)
        }
    }

    private suspend fun ProtectedAccess.opLocU(
        target: BoundLocInfo,
        base: BoundLocInfo,
        invSlot: Int,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
    ) {
        val script = opTrigger(target, base, locType, objType, invSlot)
        if (script != null) {
            eventBus.publish(this, script)
            return
        }
        mes(constants.dm_default, ChatType.Engine)
        logger.debug {
            "opLocU for `${objType.name}` on `${locType.name}` is not implemented: " +
                "locType=$locType, objType=$objType"
        }
    }

    public fun ProtectedAccess.opTrigger(
        target: BoundLocInfo,
        base: BoundLocInfo,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
        invSlot: Int,
    ): OpEvent? {
        val multiLoc = multiLoc(target, locType, player.vars)
        if (multiLoc != null) {
            val multiLocType = locTypes[multiLoc]
            val multiLocTrigger = opTrigger(multiLoc, base, multiLocType, objType, invSlot)
            if (multiLocTrigger != null) {
                return multiLocTrigger
            }
        }

        val typeEvent = LocUEvents.Op(target, locType, base, objType, invSlot)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val typeContentEvent = LocUContentEvents.OpType(target, locType, base, objType, invSlot)
        if (eventBus.contains(typeContentEvent::class.java, typeContentEvent.id)) {
            return typeContentEvent
        }

        val defaultTypeScript = LocUDefaultEvents.OpType(target, locType, base, objType, invSlot)
        if (eventBus.contains(defaultTypeScript::class.java, defaultTypeScript.id)) {
            return defaultTypeScript
        }

        val objContentEvent = LocUContentEvents.OpContent(target, locType, base, objType, invSlot)
        if (eventBus.contains(objContentEvent::class.java, objContentEvent.id)) {
            return objContentEvent
        }

        val defGroupScript = LocUDefaultEvents.OpContent(target, locType, base, objType, invSlot)
        if (eventBus.contains(defGroupScript::class.java, defGroupScript.id)) {
            return defGroupScript
        }

        return null
    }

    public suspend fun interactAp(
        access: ProtectedAccess,
        target: BoundLocInfo,
        base: BoundLocInfo,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
        inv: Inventory,
        invSlot: Int,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(inv, obj, objType)) {
            access.apLocU(target, base, locType, objType, invSlot)
        }
    }

    private suspend fun ProtectedAccess.apLocU(
        target: BoundLocInfo,
        base: BoundLocInfo,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
        invSlot: Int,
    ) {
        val script = apTrigger(target, base, locType, objType, invSlot)
        if (script != null) {
            eventBus.publish(this, script)
            return
        }
        apRange(-1)
    }

    private fun ProtectedAccess.apTrigger(
        target: BoundLocInfo,
        base: BoundLocInfo,
        locType: UnpackedLocType,
        objType: UnpackedObjType,
        invSlot: Int,
    ): ApEvent? {
        val multiLoc = multiLoc(target, locType, player.vars)
        if (multiLoc != null) {
            val multiLocType = locTypes[multiLoc]
            val multiLocTrigger = apTrigger(multiLoc, base, multiLocType, objType, invSlot)
            if (multiLocTrigger != null) {
                return multiLocTrigger
            }
        }

        val typeEvent = LocUEvents.Ap(target, locType, base, objType, invSlot)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val locContentEvent = LocUContentEvents.ApType(target, locType, base, objType, invSlot)
        if (eventBus.contains(locContentEvent::class.java, locContentEvent.id)) {
            return locContentEvent
        }

        val defaultTypeScript = LocUDefaultEvents.ApType(target, locType, base, objType, invSlot)
        if (eventBus.contains(defaultTypeScript::class.java, defaultTypeScript.id)) {
            return defaultTypeScript
        }

        val objContentEvent = LocUContentEvents.ApContent(target, locType, base, objType, invSlot)
        if (eventBus.contains(objContentEvent::class.java, objContentEvent.id)) {
            return objContentEvent
        }

        val defGroupScript = LocUDefaultEvents.ApContent(target, locType, base, objType, invSlot)
        if (eventBus.contains(defGroupScript::class.java, defGroupScript.id)) {
            return defGroupScript
        }

        return null
    }

    public fun multiLoc(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        vars: VarPlayerIntMap,
    ): BoundLocInfo? {
        if (type.multiLoc.isEmpty() && type.multiLocDefault <= 0) {
            return null
        }
        val varValue = type.multiVarValue(vars) ?: 0
        val multiLoc =
            if (varValue in type.multiLoc.indices) {
                type.multiLoc[varValue].toInt() and 0xFFFF
            } else {
                type.multiLocDefault
            }
        return if (!locTypes.containsKey(multiLoc)) {
            null
        } else {
            loc.copy(entity = LocEntity(multiLoc, loc.shapeId, loc.angleId))
        }
    }

    private fun UnpackedLocType.multiVarValue(vars: VarPlayerIntMap): Int? {
        if (multiVarp > 0) {
            val varp = varpTypes[multiVarp] ?: return null
            return vars[varp]
        } else if (multiVarBit > 0) {
            val varBit = varBitTypes[multiVarBit] ?: return null
            val packed = vars[varBit.baseVar]
            return packed.getBits(varBit.bits)
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
