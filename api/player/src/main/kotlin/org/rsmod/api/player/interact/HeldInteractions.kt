package org.rsmod.api.player.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import kotlin.math.min
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.invtx.invDel
import org.rsmod.api.invtx.invSwap
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.dialogue.Dialogues
import org.rsmod.api.player.dialogue.startDialogue
import org.rsmod.api.player.events.interact.HeldContentEvents
import org.rsmod.api.player.events.interact.HeldDropEvents
import org.rsmod.api.player.events.interact.HeldObjEvents
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.objExamine
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.worn.HeldEquipOp
import org.rsmod.api.player.worn.HeldEquipResult
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.HeldOp
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.Obj
import org.rsmod.game.obj.ObjEntity
import org.rsmod.game.obj.ObjScope
import org.rsmod.game.obj.isType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.map.CoordGrid

public class HeldInteractions
@Inject
private constructor(
    private val objTypes: ObjTypeList,
    private val eventBus: EventBus,
    private val marketPrices: MarketPrices,
    private val dropOp: InvDropOp,
    private val equipOp: HeldEquipOp,
) {
    private val logger = InlineLogger()

    public suspend fun interact(
        access: ProtectedAccess,
        inventory: Inventory,
        invSlot: Int,
        op: HeldOp,
    ) {
        val obj = inventory[invSlot] ?: return resendSlot(access.player, inventory, 0)
        interact(access, inventory, invSlot, obj, objTypes[obj], op)
    }

    /**
     * Directly drops the obj in [invSlot], bypassing the normal `Op5` scripted logic.
     *
     * Use this when an obj requires custom or additional logic (before dropping) that isn't part of
     * the usual event flow. After that custom logic, call `drop(...)` to finalize the drop as if
     * `Op5` had been invoked, but without re-triggering any event-based scripts.
     */
    public suspend fun drop(access: ProtectedAccess, inventory: Inventory, invSlot: Int) {
        val obj = inventory[invSlot]
        if (obj == null) {
            resendSlot(access.player, inventory, 0)
            return
        }

        val type = objTypes[obj]
        if (!objectVerify(access.player, inventory, obj, type, HeldOp.Op5)) {
            return
        }

        dropOp.attemptDrop(access, invSlot, obj, type)
    }

    /**
     * Directly equips the obj in [invSlot], bypassing the normal `Op2` scripted logic.
     *
     * Use this when an obj requires custom or additional logic (before equipping) that isn't part
     * of the usual event flow. After that custom logic, call `equip(...)` to finalize the equip as
     * if `Op2` had been invoked, but without re-triggering any event-based scripts.
     *
     * _Note that this function may not actually equip the obj if the player is prohibited from
     * doing so. In those cases, the reason is returned in the form of [HeldEquipResult]._
     *
     * @return the outcome of the equip attempt, represented as [HeldEquipResult].
     */
    public fun equip(access: ProtectedAccess, inventory: Inventory, invSlot: Int): HeldEquipResult {
        val obj = inventory[invSlot]
        if (obj == null) {
            resendSlot(access.player, inventory, 0)
            return HeldEquipResult.Fail.InvalidObj
        }

        val type = objTypes[obj]
        if (!objectVerify(access.player, inventory, obj, type, HeldOp.Op2)) {
            return HeldEquipResult.Fail.InvalidObj
        }

        val result = equipOp.equip(access.player, invSlot, inventory)
        return result
    }

    public fun examine(player: Player, inventory: Inventory, invSlot: Int) {
        val obj = inventory[invSlot] ?: return resendSlot(player, inventory, 0)
        objExamine(player, obj, objTypes[obj])
    }

    private suspend fun interact(
        access: ProtectedAccess,
        inventory: Inventory,
        invSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
        op: HeldOp,
    ) {
        if (!objectVerify(access.player, inventory, obj, type, op)) {
            return
        }
        when (op) {
            HeldOp.Op1 -> access.opHeld1(obj, type, inventory, invSlot)
            HeldOp.Op2 -> access.opHeld2(obj, type, inventory, invSlot)
            HeldOp.Op3 -> access.opHeld3(obj, type, inventory, invSlot)
            HeldOp.Op4 -> access.opHeld4(obj, type, inventory, invSlot)
            HeldOp.Op5 -> access.opHeld5(obj, type, inventory, invSlot)
            HeldOp.Op6 -> access.opHeld6(obj, type, inventory, invSlot)
            HeldOp.Op7 -> access.opHeld7(obj, type, inventory, invSlot)
        }
    }

    private suspend fun ProtectedAccess.opHeld1(
        obj: InvObj,
        type: UnpackedObjType,
        inventory: Inventory,
        invSlot: Int,
    ) {
        val typeScript = eventBus.suspend[HeldObjEvents.Op1::class.java, type.id]
        if (typeScript != null) {
            typeScript(HeldObjEvents.Op1(invSlot, obj, type, inventory))
            return
        }
        val groupScript = eventBus.suspend[HeldContentEvents.Op1::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(HeldContentEvents.Op1(invSlot, obj, type, inventory))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpHeld1 for `${type.name}` is not implemented: type=$type" }
    }

    private suspend fun ProtectedAccess.opHeld2(
        obj: InvObj,
        type: UnpackedObjType,
        inventory: Inventory,
        invSlot: Int,
    ) {
        val typeScript = eventBus.suspend[HeldObjEvents.Op2::class.java, type.id]
        if (typeScript != null) {
            typeScript(HeldObjEvents.Op2(invSlot, obj, type, inventory))
            return
        }
        val groupScript = eventBus.suspend[HeldContentEvents.Op2::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(HeldContentEvents.Op2(invSlot, obj, type, inventory))
            return
        }
        if (type.iop[1] != "Wield" && type.iop[1] != "Wear") {
            mes(constants.dm_default)
            logger.debug { "OpHeld2 for `${type.name}` is not implemented: type=$type" }
            return
        }
        val result = equipOp.equip(player, invSlot, inventory)
        if (result is HeldEquipResult.Fail) {
            result.messages.forEach(::mes)
        }
    }

    private suspend fun ProtectedAccess.opHeld3(
        obj: InvObj,
        type: UnpackedObjType,
        inventory: Inventory,
        invSlot: Int,
    ) {
        val typeScript = eventBus.suspend[HeldObjEvents.Op3::class.java, type.id]
        if (typeScript != null) {
            typeScript(HeldObjEvents.Op3(invSlot, obj, type, inventory))
            return
        }
        val groupScript = eventBus.suspend[HeldContentEvents.Op3::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(HeldContentEvents.Op3(invSlot, obj, type, inventory))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpHeld3 for `${type.name}` is not implemented: type=$type" }
    }

    private suspend fun ProtectedAccess.opHeld4(
        obj: InvObj,
        type: UnpackedObjType,
        inventory: Inventory,
        invSlot: Int,
    ) {
        val typeScript = eventBus.suspend[HeldObjEvents.Op4::class.java, type.id]
        if (typeScript != null) {
            typeScript(HeldObjEvents.Op4(invSlot, obj, type, inventory))
            return
        }
        val groupScript = eventBus.suspend[HeldContentEvents.Op4::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(HeldContentEvents.Op4(invSlot, obj, type, inventory))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpHeld4 for `${type.name}` is not implemented: type=$type" }
    }

    private suspend fun ProtectedAccess.opHeld5(
        obj: InvObj,
        type: UnpackedObjType,
        inventory: Inventory,
        invSlot: Int,
    ) {
        val typeScript = eventBus.suspend[HeldObjEvents.Op5::class.java, type.id]
        if (typeScript != null) {
            typeScript(HeldObjEvents.Op5(invSlot, obj, type, inventory))
            return
        }
        val groupScript = eventBus.suspend[HeldContentEvents.Op5::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(HeldContentEvents.Op5(invSlot, obj, type, inventory))
            return
        }
        dropOp.dropOrDestroy(this, invSlot, obj, type)
    }

    private suspend fun ProtectedAccess.opHeld6(
        obj: InvObj,
        type: UnpackedObjType,
        inventory: Inventory,
        invSlot: Int,
    ) {
        val typeScript = eventBus.suspend[HeldObjEvents.Op6::class.java, type.id]
        if (typeScript != null) {
            typeScript(HeldObjEvents.Op6(invSlot, obj, type, inventory))
            return
        }
        val groupScript = eventBus.suspend[HeldContentEvents.Op6::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(HeldContentEvents.Op6(invSlot, obj, type, inventory))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpHeld6 for `${type.name}` is not implemented: type=$type" }
    }

    private suspend fun ProtectedAccess.opHeld7(
        obj: InvObj,
        type: UnpackedObjType,
        inventory: Inventory,
        invSlot: Int,
    ) {
        val typeScript = eventBus.suspend[HeldObjEvents.Op7::class.java, type.id]
        if (typeScript != null) {
            typeScript(HeldObjEvents.Op7(invSlot, obj, type, inventory))
            return
        }
        val groupScript = eventBus.suspend[HeldContentEvents.Op7::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(HeldContentEvents.Op7(invSlot, obj, type, inventory))
            return
        }
        mes(constants.dm_default)
        logger.debug { "OpHeld7 for `${type.name}` is not implemented: type=$type" }
    }

    public fun drag(
        player: Player,
        inventory: Inventory,
        fromSlot: Int,
        intoSlot: Int,
        selectedObj: UnpackedObjType?,
        targetObj: UnpackedObjType?,
    ) {
        val fromObj = inventory[fromSlot]
        val intoObj = inventory[intoSlot]

        // Note: `targetObj` is the obj being dragged and `selectedObj` the one being targeted due
        // to client-sided prediction from cs2.
        if (targetObj?.id != fromObj?.id || selectedObj?.id != intoObj?.id) {
            resendSlot(player, inventory, 0)
            return
        }

        if (player.isDelayed) {
            resendSlot(player, inventory, 0)
            return
        }

        player.invSwap(inventory, fromSlot, intoSlot)
    }

    private fun objectVerify(
        player: Player,
        inventory: Inventory,
        obj: InvObj?,
        type: UnpackedObjType,
        op: HeldOp,
    ): Boolean {
        if (player.isDelayed || !obj.isType(type)) {
            resendSlot(player, inventory, 0)
            return false
        }

        // Op5 (`Drop`) always exists as a fallback.
        if (!type.hasInvOp(op) && op != HeldOp.Op5) {
            logger.debug { "OpHeld invalid op blocked: op=$op, obj=$obj, type=$type" }
            return false
        }

        return true
    }

    private fun objExamine(player: Player, obj: InvObj, type: UnpackedObjType) {
        player.objExamine(type, obj.count, marketPrices[type] ?: 0)
    }
}

private class InvDropOp
@Inject
constructor(
    private val eventBus: EventBus,
    private val objRepo: ObjRepository,
    private val marketPrices: MarketPrices,
    private val dialogues: Dialogues,
) {
    suspend fun dropOrDestroy(
        access: ProtectedAccess,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        when (type.iop[4]) {
            "Destroy" -> access.attemptDestroy(dropSlot, obj, type)
            "Release" -> access.attemptRelease(dropSlot, obj, type)
            else -> attemptDrop(access, dropSlot, obj, type)
        }
    }

    private suspend fun ProtectedAccess.attemptDestroy(
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        startDialogue(dialogues) { destroyWarning(dropSlot, obj, type) }
    }

    private suspend fun Dialogue.destroyWarning(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        val header = type.param(params.destroy_note_title)
        val text = type.param(params.destroy_note_desc)
        val confirm = confirmDestroy(type, obj.count, header, text)
        if (!confirm) {
            return
        }
        destroy(player, dropSlot, obj, type)
    }

    private fun destroy(player: Player, dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        val result = player.invDel(player.inv, type, count = obj.count, slot = dropSlot)
        if (result.success) {
            val event = HeldDropEvents.Destroy(player, dropSlot, obj, type)
            eventBus.publish(event)
        }
    }

    private suspend fun ProtectedAccess.attemptRelease(
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        if (obj.count == 1) {
            release(player, dropSlot, obj, type)
            return
        }
        startDialogue(dialogues) { releaseWarning(dropSlot, obj, type) }
    }

    private suspend fun Dialogue.releaseWarning(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        val header =
            type.paramOrNull(params.release_note_title) ?: "Drop all of your ${type.lowercaseName}?"
        val confirm = choice2("Yes", true, "No", false, title = header)
        if (!confirm) {
            return
        }
        release(player, dropSlot, obj, type)
    }

    private fun release(player: Player, dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        val result = player.invDel(player.inv, type, count = obj.count, slot = dropSlot)
        if (result.success) {
            val event = HeldDropEvents.Release(player, dropSlot, obj, type)
            eventBus.publish(event)

            val message = type.paramOrNull(params.release_note_message)
            message?.let(player::mes)
        }
    }

    suspend fun attemptDrop(
        access: ProtectedAccess,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        val player = access.player
        val trigger = player.dropTrigger
        if (trigger != null) {
            player.clearDropTrigger(trigger)
            val event = HeldDropEvents.Trigger(player, dropSlot, obj, type, trigger)
            eventBus.publish(event)
        }

        // If drop trigger was reset it means the inv obj cannot be dropped.
        if (player.dropTrigger != null) {
            return
        }

        val thresholdWarning = player.vars[varbits.drop_item_warning] == 1
        if (thresholdWarning) {
            val threshold = player.vars[varbits.drop_item_minimum_value] ?: 0
            val cost = (marketPrices[type] ?: 0) * obj.count
            if (cost >= threshold) {
                access.dropWithWarning(dropSlot, obj, type)
                return
            }
        }

        player.drop(dropSlot, obj, type)
    }

    private fun Player.drop(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        val dropped = invDropSlot(objRepo, dropSlot)
        if (!dropped) {
            return
        }
        soundSynth(synths.put_down)

        val event = HeldDropEvents.Drop(this, dropSlot, obj, type)
        eventBus.publish(event)
    }

    private suspend fun ProtectedAccess.dropWithWarning(
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        startDialogue(dialogues) { dropWarning(dropSlot, obj, type) }
    }

    private suspend fun Dialogue.dropWarning(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        objbox(
            obj = type,
            zoom = 400,
            "The item you are trying to put down " +
                "is considered <col=7f0000>valuable</col>. " +
                "Are you absolutely sure you want to do that?",
        )
        val confirm =
            choice2(
                "Put it down.",
                true,
                "No, don't put it down.",
                false,
                title = "${type.name}: Really put it down?",
            )
        if (confirm) {
            player.drop(dropSlot, obj, type)
        }
    }

    private fun Player.invDropSlot(
        repo: ObjRepository,
        slot: Int,
        count: Int = Int.MAX_VALUE,
        duration: Int = this.lootDropDuration ?: constants.lootdrop_duration,
        reveal: Int = duration - ObjRepository.DEFAULT_REVEAL_DELTA,
        coords: CoordGrid = this.coords,
        inv: Inventory = this.inv,
    ): Boolean {
        val invObj = inv[slot] ?: return false
        val cappedCount = min(invObj.count, count)
        if (cappedCount <= 0) {
            return false
        }

        val transaction = invDel(inv, invObj.id, cappedCount, slot)
        if (!transaction.success) {
            return false
        }

        val observer = observerUUID ?: error("`observerUUID` not set for player: $this")
        val entity =
            ObjEntity(id = invObj.id, count = transaction.completed(), scope = ObjScope.Private.id)
        val obj = Obj(coords, entity, currentMapClock, observer)
        repo.add(obj, duration, reveal)
        return true
    }
}
