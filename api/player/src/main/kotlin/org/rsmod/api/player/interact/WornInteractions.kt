package org.rsmod.api.player.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.params
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.events.interact.WornObjContentEvents
import org.rsmod.api.player.events.interact.WornObjEvents
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.output.objExamine
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.worn.WornUnequipOp
import org.rsmod.api.player.worn.WornUnequipResult
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.isType
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.param.ParamType

public class WornInteractions
@Inject
constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val unequipOp: WornUnequipOp,
    private val marketPrices: MarketPrices,
) {
    private val logger = InlineLogger()

    public suspend fun interact(
        access: ProtectedAccess,
        worn: Inventory,
        wornSlot: Int,
        op: IfButtonOp,
    ) {
        val obj = worn[wornSlot] ?: return resendSlot(worn, 0)
        interact(access, worn, wornSlot, obj, objTypes[obj], op)
    }

    /**
     * Directly unequips the obj in [wornSlot], bypassing the normal `Op1` scripted logic.
     *
     * Use this when an obj requires custom or additional logic (before unequipping) that isn't part
     * of the usual event flow. After that custom logic, call `unequip(...)` to finalize the equip
     * as if `Op1` had been invoked, but without re-triggering any event-based scripts.
     *
     * _Note that this function may not actually unequip the obj if the player is prohibited from
     * doing so. In those cases, the reason is returned in the form of [WornUnequipResult]._
     *
     * @return the outcome of the unequip attempt, represented as [WornUnequipResult].
     */
    public fun unequip(
        access: ProtectedAccess,
        worn: Inventory,
        wornSlot: Int,
        into: Inventory,
    ): WornUnequipResult {
        val obj = worn[wornSlot]
        if (obj == null) {
            resendSlot(worn, 0)
            return WornUnequipResult.Fail.InvalidObj
        }

        val type = objTypes[obj]
        if (!objectVerify(access.player, worn, obj, type, IfButtonOp.Op1)) {
            return WornUnequipResult.Fail.InvalidObj
        }

        val result = unequipOp.unequip(access.player, wornSlot, worn, into)
        return result
    }

    public fun examine(player: Player, worn: Inventory, wornSlot: Int) {
        val obj = worn[wornSlot] ?: return resendSlot(worn, 0)
        objExamine(player, obj, objTypes[obj])
    }

    private suspend fun interact(
        access: ProtectedAccess,
        worn: Inventory,
        wornSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
        op: IfButtonOp,
    ) {
        if (!objectVerify(access.player, worn, obj, type, op)) {
            return
        }
        when (op) {
            IfButtonOp.Op1 -> access.wornOp1(obj, type, worn, wornSlot)
            IfButtonOp.Op2 -> access.opWorn2(obj, type, wornSlot)
            IfButtonOp.Op3 -> access.opWorn3(obj, type, wornSlot)
            IfButtonOp.Op4 -> access.opWorn4(obj, type, wornSlot)
            IfButtonOp.Op5 -> access.opWorn5(obj, type, wornSlot)
            IfButtonOp.Op6 -> access.opWorn6(obj, type, wornSlot)
            IfButtonOp.Op7 -> access.opWorn7(obj, type, wornSlot)
            IfButtonOp.Op8 -> access.opWorn8(obj, type, wornSlot)
            IfButtonOp.Op9 -> access.opWorn9(obj, type, wornSlot)
            IfButtonOp.Op10 -> examine(access.player, worn, wornSlot)
        }
    }

    private suspend fun ProtectedAccess.wornOp1(
        obj: InvObj,
        type: UnpackedObjType,
        worn: Inventory,
        wornSlot: Int,
    ) {
        val typeScript = eventBus.suspend[WornObjEvents.Op1::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op1(wornSlot, obj))
            return
        }
        val groupScript = eventBus.suspend[WornObjContentEvents.Op1::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op1(wornSlot, obj, type))
            return
        }
        val result = unequipOp.unequip(player, wornSlot, worn, into = inv)
        if (result is WornUnequipResult.Fail) {
            result.message?.let(::mes)
        }
    }

    private suspend fun ProtectedAccess.opWorn2(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.suspend[WornObjEvents.Op2::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op2(wornSlot, obj))
            return
        }
        val groupScript = eventBus.suspend[WornObjContentEvents.Op2::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op2(wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpWorn2 for `${type.name}` is not implemented: type=$type" }
    }

    private suspend fun ProtectedAccess.opWorn3(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.suspend[WornObjEvents.Op3::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op3(wornSlot, obj))
            return
        }
        val groupScript = eventBus.suspend[WornObjContentEvents.Op3::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op3(wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpWorn3 for `${type.name}` is not implemented: type=$type" }
    }

    private suspend fun ProtectedAccess.opWorn4(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.suspend[WornObjEvents.Op4::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op4(wornSlot, obj))
            return
        }
        val groupScript = eventBus.suspend[WornObjContentEvents.Op4::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op4(wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpWorn4 for `${type.name}` is not implemented: type=$type" }
    }

    private suspend fun ProtectedAccess.opWorn5(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.suspend[WornObjEvents.Op5::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op5(wornSlot, obj))
            return
        }
        val groupScript = eventBus.suspend[WornObjContentEvents.Op5::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op5(wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpWorn5 for `${type.name}` is not implemented: type=$type" }
    }

    private suspend fun ProtectedAccess.opWorn6(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.suspend[WornObjEvents.Op6::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op6(wornSlot, obj))
            return
        }
        val groupScript = eventBus.suspend[WornObjContentEvents.Op6::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op6(wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpWorn6 for `${type.name}` is not implemented: type=$type" }
    }

    private suspend fun ProtectedAccess.opWorn7(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.suspend[WornObjEvents.Op7::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op7(wornSlot, obj))
            return
        }
        val groupScript = eventBus.suspend[WornObjContentEvents.Op7::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op7(wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpWorn7 for `${type.name}` is not implemented: type=$type" }
    }

    private suspend fun ProtectedAccess.opWorn8(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.suspend[WornObjEvents.Op8::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op8(wornSlot, obj))
            return
        }
        val groupScript = eventBus.suspend[WornObjContentEvents.Op8::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op8(wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpWorn8 for `${type.name}` is not implemented: type=$type" }
    }

    private suspend fun ProtectedAccess.opWorn9(obj: InvObj, type: UnpackedObjType, wornSlot: Int) {
        val typeScript = eventBus.suspend[WornObjEvents.Op9::class.java, type.id]
        if (typeScript != null) {
            typeScript(WornObjEvents.Op9(wornSlot, obj))
            return
        }
        val groupScript = eventBus.suspend[WornObjContentEvents.Op9::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(WornObjContentEvents.Op9(wornSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpWorn9 for `${type.name}` is not implemented: type=$type" }
    }

    private fun objectVerify(
        player: Player,
        inventory: Inventory,
        obj: InvObj?,
        type: UnpackedObjType,
        op: IfButtonOp,
    ): Boolean {
        if (player.isDelayed || !obj.isType(type)) {
            resendSlot(inventory, 0)
            return false
        }

        // Op1 (`Remove`) and Op10 (`Examine`) always exists for worn objs.
        val param = op.toParamType()
        if (param != null && !type.hasParam(param)) {
            logger.debug { "OpWorn invalid op blocked: op=$op, obj=$obj, type=$type" }
            resendSlot(inventory, 0)
            return false
        }

        return true
    }

    private fun IfButtonOp.toParamType(): ParamType<String>? =
        when (this) {
            IfButtonOp.Op1 -> null
            IfButtonOp.Op2 -> params.wear_op1
            IfButtonOp.Op3 -> params.wear_op2
            IfButtonOp.Op4 -> params.wear_op3
            IfButtonOp.Op5 -> params.wear_op4
            IfButtonOp.Op6 -> params.wear_op5
            IfButtonOp.Op7 -> params.wear_op6
            IfButtonOp.Op8 -> params.wear_op7
            IfButtonOp.Op9 -> params.wear_op8
            IfButtonOp.Op10 -> null
        }

    private fun objExamine(player: Player, obj: InvObj, type: UnpackedObjType) {
        player.objExamine(type, obj.count, marketPrices[type] ?: 0)
    }
}
