package org.rsmod.api.interactions

import jakarta.inject.Inject
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.ObjContentEvents
import org.rsmod.api.player.events.interact.ObjDefaultEvents
import org.rsmod.api.player.events.interact.ObjEvents
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionObj
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType

public class ObjInteractions
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val registry: ObjRegistry,
    private val eventBus: EventBus,
    private val protectedAccess: ProtectedAccessLauncher,
) {
    public fun triggerOp(player: Player, interaction: InteractionObj) {
        val obj = interaction.target
        val op = opTrigger(player, obj, objTypes[obj], interaction.opSlot)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    @Suppress("unused")
    public fun opTrigger(player: Player, obj: Obj, type: UnpackedObjType, op: Int): OpEvent? {
        val opEvent = obj.toOp(op)
        if (opEvent != null && eventBus.contains(opEvent::class.java, type.id)) {
            return opEvent
        }
        val contentEvent = obj.toContentOp(type.contentType, op)
        if (contentEvent != null && eventBus.contains(contentEvent::class.java, type.contentType)) {
            return contentEvent
        }
        val defaultEvent = obj.toDefaultOp(op)
        if (defaultEvent != null && eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }
        return null
    }

    public fun hasOpTrigger(player: Player, obj: Obj, type: UnpackedObjType, op: Int): Boolean =
        opTrigger(player, obj, type, op) != null

    public fun triggerAp(player: Player, interaction: InteractionObj) {
        val obj = interaction.target
        val ap = apTrigger(player, obj, objTypes[obj], interaction.opSlot)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }

    @Suppress("unused")
    public fun apTrigger(player: Player, obj: Obj, type: UnpackedObjType, op: Int): ApEvent? {
        val apEvent = obj.toAp(op)
        if (apEvent != null && eventBus.contains(apEvent::class.java, type.id)) {
            return apEvent
        }
        val contentEvent = obj.toContentAp(type.contentType, op)
        if (contentEvent != null && eventBus.contains(contentEvent::class.java, type.contentType)) {
            return contentEvent
        }
        val defaultEvent = obj.toDefaultAp(op)
        if (defaultEvent != null && eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }
        return null
    }

    public fun hasApTrigger(player: Player, obj: Obj, type: UnpackedObjType, op: Int): Boolean =
        apTrigger(player, obj, type, op) != null

    private fun Obj.toOp(op: Int): ObjEvents.Op? =
        when (op) {
            1 -> ObjEvents.Op1(this)
            2 -> ObjEvents.Op2(this)
            3 -> ObjEvents.Op3(this)
            4 -> ObjEvents.Op4(this)
            5 -> ObjEvents.Op5(this)
            else -> null
        }

    private fun Obj.toContentOp(contentType: Int, op: Int): ObjContentEvents.Op? =
        when (op) {
            1 -> ObjContentEvents.Op1(this, contentType)
            2 -> ObjContentEvents.Op2(this, contentType)
            3 -> ObjContentEvents.Op3(this, contentType)
            4 -> ObjContentEvents.Op4(this, contentType)
            5 -> ObjContentEvents.Op5(this, contentType)
            else -> null
        }

    private fun Obj.toDefaultOp(op: Int): ObjDefaultEvents.Op? =
        when (op) {
            1 -> ObjDefaultEvents.Op1(this)
            2 -> ObjDefaultEvents.Op2(this)
            3 -> ObjDefaultEvents.Op3(this)
            4 -> ObjDefaultEvents.Op4(this)
            5 -> ObjDefaultEvents.Op5(this)
            else -> null
        }

    private fun Obj.toAp(op: Int): ObjEvents.Ap? =
        when (op) {
            1 -> ObjEvents.Ap1(this)
            2 -> ObjEvents.Ap2(this)
            3 -> ObjEvents.Ap3(this)
            4 -> ObjEvents.Ap4(this)
            5 -> ObjEvents.Ap5(this)
            else -> null
        }

    private fun Obj.toContentAp(contentType: Int, op: Int): ObjContentEvents.Ap? =
        when (op) {
            1 -> ObjContentEvents.Ap1(this, contentType)
            2 -> ObjContentEvents.Ap2(this, contentType)
            3 -> ObjContentEvents.Ap3(this, contentType)
            4 -> ObjContentEvents.Ap4(this, contentType)
            5 -> ObjContentEvents.Ap5(this, contentType)
            else -> null
        }

    private fun Obj.toDefaultAp(op: Int): ObjDefaultEvents.Ap? =
        when (op) {
            1 -> ObjDefaultEvents.Ap1(this)
            2 -> ObjDefaultEvents.Ap1(this)
            3 -> ObjDefaultEvents.Ap1(this)
            4 -> ObjDefaultEvents.Ap1(this)
            5 -> ObjDefaultEvents.Ap1(this)
            else -> null
        }
}
