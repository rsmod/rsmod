package org.rsmod.api.player.interact

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.invtx.invDel
import org.rsmod.api.invtx.invDropSlot
import org.rsmod.api.invtx.invSwap
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.dialogue.Dialogues
import org.rsmod.api.player.dialogue.startDialogue
import org.rsmod.api.player.events.interact.InvObjContentEvents
import org.rsmod.api.player.events.interact.InvObjDropEvents
import org.rsmod.api.player.events.interact.InvObjEvents
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.objExamine
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.worn.InvEquipOp
import org.rsmod.api.player.worn.InvEquipResult
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InvInteractionOp
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.isType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType

public class InvInteractions
@Inject
private constructor(
    private val objTypes: ObjTypeList,
    private val eventBus: EventBus,
    private val marketPrices: MarketPrices,
    private val dropOp: InvDropOp,
    private val equipOp: InvEquipOp,
) {
    private val logger = InlineLogger()

    public fun interact(player: Player, inv: Inventory, invSlot: Int, op: InvInteractionOp) {
        val obj = inv[invSlot]
        if (obj == null) {
            resendSlot(player, inv, 0)
            return
        }
        interact(player, inv, invSlot, obj, objTypes[obj], op)
    }

    private fun interact(
        player: Player,
        inv: Inventory,
        invSlot: Int,
        obj: InvObj,
        type: UnpackedObjType,
        op: InvInteractionOp,
    ) {
        if (op == InvInteractionOp.Op8) {
            player.objExamine(type, obj.count, marketPrices[type] ?: 0)
            return
        }

        // Op5 (`Drop`) always exists as a fallback.
        if (!type.hasInvOp(op) && op != InvInteractionOp.Op5) {
            logger.debug { "InvOp invalid op blocked: op=$op, obj=$obj, type=$type" }
            return
        }

        if (player.isDelayed || !obj.isType(type)) {
            resendSlot(player, inv, 0)
            return
        }

        player.clearPendingAction(eventBus)
        player.resetFaceEntity()

        when (op) {
            InvInteractionOp.Op1 -> player.invOp1(obj, type, invSlot)
            InvInteractionOp.Op2 -> player.invOp2(obj, type, invSlot, inv)
            InvInteractionOp.Op3 -> player.invOp3(obj, type, invSlot)
            InvInteractionOp.Op4 -> player.invOp4(obj, type, invSlot)
            InvInteractionOp.Op5 -> player.invOp5(obj, type, invSlot)
            InvInteractionOp.Op6 -> player.invOp6(obj, type, invSlot)
            InvInteractionOp.Op7 -> player.invOp7(obj, type, invSlot)
            InvInteractionOp.Op8 -> throw IllegalStateException("Unreachable.")
        }
    }

    private fun Player.invOp1(obj: InvObj, type: UnpackedObjType, invSlot: Int) {
        val typeScript = eventBus.keyed[InvObjEvents.Op1::class.java, type.id]
        if (typeScript != null) {
            typeScript(InvObjEvents.Op1(this, invSlot, obj, type))
            return
        }
        val groupScript = eventBus.keyed[InvObjContentEvents.Op1::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(InvObjContentEvents.Op1(this, invSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "InvOp1 for `${type.name}` is not implemented: type=$type" }
    }

    private fun Player.invOp2(obj: InvObj, type: UnpackedObjType, invSlot: Int, inv: Inventory) {
        val typeScript = eventBus.keyed[InvObjEvents.Op2::class.java, type.id]
        if (typeScript != null) {
            typeScript(InvObjEvents.Op2(this, invSlot, obj, type))
            return
        }
        val groupScript = eventBus.keyed[InvObjContentEvents.Op2::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(InvObjContentEvents.Op2(this, invSlot, obj, type))
            return
        }
        if (type.iop[1] != "Wield" && type.iop[1] != "Wear") {
            mes(constants.dm_default)
            logger.debug { "InvOp2 for `${type.name}` is not implemented: type=$type" }
            return
        }
        val result = equipOp.equip(this, invSlot, inv)
        if (result is InvEquipResult.Fail) {
            result.messages.forEach(::mes)
        }
    }

    private fun Player.invOp3(obj: InvObj, type: UnpackedObjType, invSlot: Int) {
        val typeScript = eventBus.keyed[InvObjEvents.Op3::class.java, type.id]
        if (typeScript != null) {
            typeScript(InvObjEvents.Op3(this, invSlot, obj, type))
            return
        }
        val groupScript = eventBus.keyed[InvObjContentEvents.Op3::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(InvObjContentEvents.Op3(this, invSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "InvOp3 for `${type.name}` is not implemented: type=$type" }
    }

    private fun Player.invOp4(obj: InvObj, type: UnpackedObjType, invSlot: Int) {
        val typeScript = eventBus.keyed[InvObjEvents.Op4::class.java, type.id]
        if (typeScript != null) {
            typeScript(InvObjEvents.Op4(this, invSlot, obj, type))
            return
        }
        val groupScript = eventBus.keyed[InvObjContentEvents.Op4::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(InvObjContentEvents.Op4(this, invSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "InvOp4 for `${type.name}` is not implemented: type=$type" }
    }

    private fun Player.invOp5(obj: InvObj, type: UnpackedObjType, invSlot: Int) {
        dropOp.dropOrDestroy(this, invSlot, obj, type)
    }

    private fun Player.invOp6(obj: InvObj, type: UnpackedObjType, invSlot: Int) {
        val typeScript = eventBus.keyed[InvObjEvents.Op6::class.java, type.id]
        if (typeScript != null) {
            typeScript(InvObjEvents.Op6(this, invSlot, obj, type))
            return
        }
        val groupScript = eventBus.keyed[InvObjContentEvents.Op6::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(InvObjContentEvents.Op6(this, invSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "InvOp6 for `${type.name}` is not implemented: type=$type" }
    }

    private fun Player.invOp7(obj: InvObj, type: UnpackedObjType, invSlot: Int) {
        val typeScript = eventBus.keyed[InvObjEvents.Op7::class.java, type.id]
        if (typeScript != null) {
            typeScript(InvObjEvents.Op7(this, invSlot, obj, type))
            return
        }
        val groupScript = eventBus.keyed[InvObjContentEvents.Op7::class.java, type.contentGroup]
        if (groupScript != null) {
            groupScript(InvObjContentEvents.Op7(this, invSlot, obj, type))
            return
        }
        mes(constants.dm_default)
        logger.debug { "InvOp7 for `${type.name}` is not implemented: type=$type" }
    }

    public fun drag(
        player: Player,
        inv: Inventory,
        fromSlot: Int,
        intoSlot: Int,
        selectedObj: UnpackedObjType?,
        targetObj: UnpackedObjType?,
    ) {
        val fromObj = inv[fromSlot]
        val intoObj = inv[intoSlot]

        // Note: `targetObj` is the obj being dragged and `selectedObj` the one being targeted due
        // to client-sided prediction from cs2.
        if (targetObj?.id != fromObj?.id || selectedObj?.id != intoObj?.id) {
            resendSlot(player, inv, 0)
            return
        }

        if (player.isDelayed) {
            resendSlot(player, inv, 0)
            return
        }

        player.ifClose(eventBus)
        player.invSwap(inv, fromSlot, intoSlot)
    }
}

private class InvDropOp
@Inject
constructor(
    private val eventBus: EventBus,
    private val objRepo: ObjRepository,
    private val protectedAccess: ProtectedAccessLauncher,
    private val marketPrices: MarketPrices,
    private val dialogues: Dialogues,
) {
    fun dropOrDestroy(player: Player, dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        when (type.iop[4]) {
            "Destroy" -> player.attemptDestroy(dropSlot, obj, type)
            "Release" -> player.attemptRelease(dropSlot, obj, type)
            else -> player.attemptDrop(dropSlot, obj, type)
        }
    }

    private fun Player.attemptDestroy(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        protectedAccess.launch(this) {
            startDialogue(dialogues) { destroyWarning(dropSlot, obj, type) }
        }
    }

    private suspend fun Dialogue.destroyWarning(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        val header = type.param(params.destroy_note_title)
        val text = type.param(params.destroy_note_desc)
        val confirm = confirmDestroy(type, obj.count, header, text)
        if (!confirm) {
            return
        }
        val result = player.invDel(player.inv, type, count = obj.count, slot = dropSlot)
        if (result.success) {
            val event = InvObjDropEvents.Destroy(player, dropSlot, obj, type)
            eventBus.publish(event)
        }
    }

    private fun Player.attemptRelease(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        if (obj.count == 1) {
            release(dropSlot, obj, type)
        } else {
            protectedAccess.launch(this) {
                startDialogue(dialogues) { releaseWarning(dropSlot, obj, type) }
            }
        }
    }

    private suspend fun Dialogue.releaseWarning(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        val header = type.param(params.release_note_title)
        val confirm = choice2("Yes", true, "No", false, title = header)
        if (!confirm) {
            return
        }
        player.release(dropSlot, obj, type)
    }

    private fun Player.release(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        val message = type.paramOrNull(params.release_note_message)
        val result = invDel(inv, type, count = obj.count, slot = dropSlot)
        if (result.success) {
            val event = InvObjDropEvents.Release(this, dropSlot, obj, type)
            eventBus.publish(event)
            message?.let(::mes)
        }
    }

    private fun Player.attemptDrop(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        val trigger = dropTrigger
        if (trigger != null) {
            clearDropTrigger(trigger)
            val event = InvObjDropEvents.Trigger(this, dropSlot, obj, type, trigger)
            eventBus.publish(event)
        }

        // If drop trigger was reset it means the inv obj cannot be dropped.
        if (dropTrigger != null) {
            return
        }

        val thresholdWarning = vars[varbits.drop_item_warning] == 1
        if (thresholdWarning) {
            val threshold = vars[varbits.drop_item_minimum_value] ?: 0
            val cost = (marketPrices[type] ?: 0) * obj.count
            if (cost >= threshold) {
                dropWithWarning(dropSlot, obj, type)
                return
            }
        }

        drop(dropSlot, obj, type)
    }

    private fun Player.drop(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        val dropped = invDropSlot(objRepo, dropSlot)
        if (!dropped) {
            return
        }
        soundSynth(synths.put_down)

        val event = InvObjDropEvents.Drop(this, dropSlot, obj, type)
        eventBus.publish(event)
    }

    private fun Player.dropWithWarning(dropSlot: Int, obj: InvObj, type: UnpackedObjType) {
        protectedAccess.launch(this) {
            startDialogue(dialogues) { dropWarning(dropSlot, obj, type) }
        }
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
}
