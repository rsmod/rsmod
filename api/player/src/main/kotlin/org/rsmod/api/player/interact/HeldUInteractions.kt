package org.rsmod.api.player.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.config.constants
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.NpcUContentEvents
import org.rsmod.api.player.events.interact.NpcUDefaultEvents
import org.rsmod.api.player.events.interact.NpcUEvents
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.isType
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.varbit.VarBitTypeList
import org.rsmod.game.type.varp.VarpTypeList
import org.rsmod.game.vars.VarPlayerIntMap
import org.rsmod.utils.bits.getBits

public class HeldUInteractions
@Inject
private constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val npcTypes: NpcTypeList,
    private val varpTypes: VarpTypeList,
    private val varBitTypes: VarBitTypeList,
) {
    private val logger = InlineLogger()

    public suspend fun interactOp(
        access: ProtectedAccess,
        target: Npc,
        inv: Inventory,
        invSlot: Int,
        npcType: UnpackedNpcType,
        objType: UnpackedObjType,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(inv, obj, objType)) {
            access.opHeldU(target, invSlot, npcType, objType)
        }
    }

    private suspend fun ProtectedAccess.opHeldU(
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
        mes(constants.dm_default)
        logger.debug {
            "opHeldU for `${objType.name}` on `${npcType.name}` is not implemented: " +
                "npcType=$npcType, objType=$objType"
        }
    }

    private fun ProtectedAccess.opTrigger(
        target: Npc,
        invSlot: Int,
        npcType: UnpackedNpcType,
        objType: UnpackedObjType,
    ): OpEvent? {
        val multiNpcType = multiNpc(npcType, player.vars)
        if (multiNpcType != null) {
            val multiNpcTrigger = opTrigger(target, invSlot, multiNpcType, objType)
            if (multiNpcTrigger != null) {
                return multiNpcTrigger
            }
        }

        val contentGroup = npcType.contentGroup

        val typeScript = NpcUEvents.Op(target, invSlot, objType, npcType)
        if (eventBus.contains(typeScript::class.java, typeScript.id)) {
            return typeScript
        }

        val groupScript = NpcUContentEvents.Op(target, invSlot, objType, contentGroup)
        if (eventBus.contains(groupScript::class.java, groupScript.id)) {
            return groupScript
        }

        val defaultTypeScript = NpcUDefaultEvents.OpType(target, invSlot, objType, npcType)
        if (eventBus.contains(defaultTypeScript::class.java, defaultTypeScript.id)) {
            return defaultTypeScript
        }

        val defGroupScript = NpcUDefaultEvents.OpContent(target, invSlot, objType, contentGroup)
        if (eventBus.contains(defGroupScript::class.java, defGroupScript.id)) {
            return defGroupScript
        }

        return null
    }

    public suspend fun interactAp(
        access: ProtectedAccess,
        target: Npc,
        inv: Inventory,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val obj = inv[invSlot]
        if (objectVerify(inv, obj, objType)) {
            access.apHeldU(target, invSlot, objType)
        }
    }

    private suspend fun ProtectedAccess.apHeldU(
        target: Npc,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        val script = apTrigger(target, invSlot, target.visType, objType)
        if (script != null) {
            eventBus.publish(this, script)
            return
        }
        apRange(-1)
    }

    private fun ProtectedAccess.apTrigger(
        target: Npc,
        invSlot: Int,
        npcType: UnpackedNpcType,
        objType: UnpackedObjType,
    ): ApEvent? {
        val multiNpcType = multiNpc(npcType, player.vars)
        if (multiNpcType != null) {
            val multiNpcTrigger = apTrigger(target, invSlot, multiNpcType, objType)
            if (multiNpcTrigger != null) {
                return multiNpcTrigger
            }
        }

        val contentGroup = npcType.contentGroup

        val typeScript = NpcUEvents.Ap(target, invSlot, objType, npcType)
        if (eventBus.contains(typeScript::class.java, typeScript.id)) {
            return typeScript
        }

        val groupScript = NpcUContentEvents.Ap(target, invSlot, objType, contentGroup)
        if (eventBus.contains(groupScript::class.java, groupScript.id)) {
            return groupScript
        }

        val defaultTypeScript = NpcUDefaultEvents.ApType(target, invSlot, objType, npcType)
        if (eventBus.contains(defaultTypeScript::class.java, defaultTypeScript.id)) {
            return defaultTypeScript
        }

        val defGroupScript = NpcUDefaultEvents.ApContent(target, invSlot, objType, contentGroup)
        if (eventBus.contains(defGroupScript::class.java, defGroupScript.id)) {
            return defGroupScript
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

    public fun multiNpc(type: UnpackedNpcType, vars: VarPlayerIntMap): UnpackedNpcType? {
        if (type.multiNpc.isEmpty() && type.multiNpcDefault <= 0) {
            return null
        }
        val varValue = type.multiVarValue(vars) ?: 0
        val multiNpc =
            if (varValue in type.multiNpc.indices) {
                type.multiNpc[varValue].toInt() and 0xFFFF
            } else {
                type.multiNpcDefault
            }
        return if (!npcTypes.containsKey(multiNpc)) {
            null
        } else {
            npcTypes.getOrDefault(multiNpc, null)
        }
    }

    private fun UnpackedNpcType.multiVarValue(vars: VarPlayerIntMap): Int? {
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
}
