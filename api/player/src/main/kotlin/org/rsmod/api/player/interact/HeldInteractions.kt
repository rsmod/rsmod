package org.rsmod.api.player.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import kotlin.math.min
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.invtx.invDel
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.events.interact.HeldContentEvents
import org.rsmod.api.player.events.interact.HeldDropEvents
import org.rsmod.api.player.events.interact.HeldObjEvents
import org.rsmod.api.player.output.ChatType
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
import org.rsmod.game.inv.InvObj
import org.rsmod.game.inv.Inventory
import org.rsmod.game.inv.isType
import org.rsmod.game.obj.Obj
import org.rsmod.game.obj.ObjEntity
import org.rsmod.game.obj.ObjScope
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.map.CoordGrid

public class HeldInteractions
@Inject
private constructor(
    private val objTypes: ObjTypeList,
    private val eventBus: EventBus,
    private val marketPrices: MarketPrices,
    private val dropOp: HeldDropOp,
    private val equipOp: HeldEquipOp,
) {
    private val logger = InlineLogger()

    public suspend fun interact(
        access: ProtectedAccess,
        inventory: Inventory,
        invSlot: Int,
        op: HeldOp,
    ) {
        val obj = inventory[invSlot] ?: return resendSlot(inventory, 0)
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
            resendSlot(inventory, 0)
            return
        }

        val type = objTypes[obj]
        if (!objectVerify(access.player, inventory, obj, type)) {
            return
        }

        dropOp.attemptDrop(access, inventory, invSlot, obj, type)
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
            resendSlot(inventory, 0)
            return HeldEquipResult.Fail.InvalidObj
        }

        val type = objTypes[obj]
        if (!objectVerify(access.player, inventory, obj, type)) {
            return HeldEquipResult.Fail.InvalidObj
        }

        val result = equipOp.equip(access.player, invSlot, inventory)
        return result
    }

    public fun examine(player: Player, inventory: Inventory, invSlot: Int) {
        val obj = inventory[invSlot] ?: return resendSlot(inventory, 0)
        val objType = objTypes[obj]
        val price = marketPrices[objType] ?: 0
        player.objExamine(objType, obj.count, price)
    }

    private suspend fun interact(
        access: ProtectedAccess,
        inventory: Inventory,
        invSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
        op: HeldOp,
    ) {
        if (!objectVerify(access.player, inventory, obj, type)) {
            return
        } else if (!hasOp(inventory, obj, type, op)) {
            return
        }
        when (op) {
            HeldOp.Op1 -> access.opHeld1(obj, type, inventory, invSlot)
            HeldOp.Op2 -> access.opHeld2(obj, type, inventory, invSlot)
            HeldOp.Op3 -> access.opHeld3(obj, type, inventory, invSlot)
            HeldOp.Op4 -> access.opHeld4(obj, type, inventory, invSlot)
            HeldOp.Op5 -> access.opHeld5(obj, type, inventory, invSlot)
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
        mes(constants.dm_default, ChatType.Engine)
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
        if (!type.isEquipable) {
            mes(constants.dm_default, ChatType.Engine)
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
        mes(constants.dm_default, ChatType.Engine)
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
        mes(constants.dm_default, ChatType.Engine)
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
        dropOp.dropOrDestroy(this, inventory, invSlot, obj, type)
    }

    private fun objectVerify(
        player: Player,
        inventory: Inventory,
        obj: InvObj?,
        type: UnpackedObjType,
    ): Boolean {
        if (player.isDelayed || !obj.isType(type)) {
            resendSlot(inventory, 0)
            return false
        }
        return true
    }

    private fun hasOp(
        inventory: Inventory,
        obj: InvObj,
        type: UnpackedObjType,
        op: HeldOp,
    ): Boolean {
        // Op5 (`Drop`) always exists as a fallback.
        if (!type.hasInvOp(op) && op != HeldOp.Op5) {
            logger.debug { "OpHeld invalid op blocked: op=$op, obj=$obj, type=$type" }
            resendSlot(inventory, 0)
            return false
        }
        return true
    }
}

private class HeldDropOp
@Inject
constructor(
    private val eventBus: EventBus,
    private val objRepo: ObjRepository,
    private val marketPrices: MarketPrices,
) {
    suspend fun dropOrDestroy(
        access: ProtectedAccess,
        inventory: Inventory,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        when (type.iop[4]) {
            "Destroy" -> access.attemptDestroy(inventory, dropSlot, obj, type)
            "Release" -> access.attemptRelease(inventory, dropSlot, obj, type)
            else -> attemptDrop(access, inventory, dropSlot, obj, type)
        }
    }

    private suspend fun ProtectedAccess.attemptDestroy(
        inventory: Inventory,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        startDialogue { destroyWarning(inventory, dropSlot, obj, type) }
    }

    private suspend fun Dialogue.destroyWarning(
        inventory: Inventory,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        val header = type.param(params.destroy_note_title)
        val text = type.param(params.destroy_note_desc)
        val confirm = confirmDestroy(type, obj.count, header, text)
        if (!confirm) {
            return
        }
        destroy(player, inventory, dropSlot, obj, type)
    }

    private fun destroy(
        player: Player,
        inventory: Inventory,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        val result = player.invDel(inventory, type, count = obj.count, slot = dropSlot)
        if (result.success) {
            val event = HeldDropEvents.Destroy(player, dropSlot, obj, type)
            eventBus.publish(event)
        }
    }

    private suspend fun ProtectedAccess.attemptRelease(
        inventory: Inventory,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        if (obj.count == 1) {
            release(player, inventory, dropSlot, obj, type)
            return
        }
        startDialogue { releaseWarning(inventory, dropSlot, obj, type) }
    }

    private suspend fun Dialogue.releaseWarning(
        inventory: Inventory,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        val header =
            type.paramOrNull(params.release_note_title) ?: "Drop all of your ${type.lowercaseName}?"
        val confirm = choice2("Yes", true, "No", false, title = header)
        if (!confirm) {
            return
        }
        release(player, inventory, dropSlot, obj, type)
    }

    private fun release(
        player: Player,
        inventory: Inventory,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        val result = player.invDel(inventory, type, count = obj.count, slot = dropSlot)
        if (result.success) {
            val event = HeldDropEvents.Release(player, dropSlot, obj, type)
            eventBus.publish(event)

            val message = type.paramOrNull(params.release_note_message)
            message?.let(player::mes)
        }
    }

    suspend fun attemptDrop(
        access: ProtectedAccess,
        inventory: Inventory,
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

        val thresholdWarning = player.vars[varbits.option_dropwarning_on] == 1
        if (thresholdWarning) {
            val threshold = player.vars[varbits.option_dropwarning_value]
            val cost = (marketPrices[type] ?: 0) * obj.count
            if (cost >= threshold) {
                access.dropWithWarning(inventory, dropSlot, obj, type)
                return
            }
        }

        player.drop(inventory, dropSlot, obj, type)
    }

    private fun Player.drop(
        inventory: Inventory,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        val dropped = invDropSlot(objRepo, inventory, dropSlot)
        if (!dropped) {
            return
        }
        soundSynth(synths.put_down)

        val event = HeldDropEvents.Drop(this, dropSlot, obj, type)
        eventBus.publish(event)
    }

    private suspend fun ProtectedAccess.dropWithWarning(
        inventory: Inventory,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
        startDialogue { dropWarning(inventory, dropSlot, obj, type) }
    }

    private suspend fun Dialogue.dropWarning(
        inventory: Inventory,
        dropSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
    ) {
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
            player.drop(inventory, dropSlot, obj, type)
        }
    }

    private fun Player.invDropSlot(
        repo: ObjRepository,
        inventory: Inventory,
        slot: Int,
        count: Int = Int.MAX_VALUE,
        duration: Int = this.lootDropDuration ?: constants.lootdrop_duration,
        reveal: Int = ObjRepository.DEFAULT_REVEAL_DELAY,
        coords: CoordGrid = this.coords,
    ): Boolean {
        val invObj = inventory[slot] ?: return false
        val cappedCount = min(invObj.count, count)
        if (cappedCount <= 0) {
            return false
        }

        val transaction = invDel(inventory, invObj.id, cappedCount, slot, autoCommit = false)
        if (!transaction.success) {
            return false
        }

        val observer = observerUUID ?: error("`observerUUID` not set for player: $this")
        val entity =
            ObjEntity(id = invObj.id, count = transaction.completed(), scope = ObjScope.Private.id)
        val obj = Obj(coords, entity, currentMapClock, observer)
        val dropped = repo.add(obj, duration, reveal)
        if (!dropped) {
            return false
        }

        transaction.commitAll()
        return true
    }
}
