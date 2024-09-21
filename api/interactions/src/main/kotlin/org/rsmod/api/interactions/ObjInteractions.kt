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
import org.rsmod.game.interact.InteractionOp
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
        val op = opTrigger(player, obj, objTypes[obj], interaction.op)
        if (op != null) {
            protectedAccess.launch(player) { eventBus.publish(this, op) }
        }
    }

    @Suppress("unused")
    public fun opTrigger(
        player: Player,
        obj: Obj,
        type: UnpackedObjType,
        op: InteractionOp,
    ): OpEvent? {
        val typeEvent = obj.toOp(op)
        if (eventBus.contains(typeEvent::class.java, type.id)) {
            return typeEvent
        }

        val contentEvent = obj.toContentOp(type.contentGroup, op)
        if (eventBus.contains(contentEvent::class.java, type.contentGroup)) {
            return contentEvent
        }

        val defaultEvent = obj.toDefaultOp(op)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasOpTrigger(
        player: Player,
        obj: Obj,
        type: UnpackedObjType,
        op: InteractionOp,
    ): Boolean = opTrigger(player, obj, type, op) != null

    public fun triggerAp(player: Player, interaction: InteractionObj) {
        val obj = interaction.target
        val ap = apTrigger(player, obj, objTypes[obj], interaction.op)
        if (ap != null) {
            protectedAccess.launch(player) { eventBus.publish(this, ap) }
        }
    }

    @Suppress("unused")
    public fun apTrigger(
        player: Player,
        obj: Obj,
        type: UnpackedObjType,
        op: InteractionOp,
    ): ApEvent? {
        val typeEvent = obj.toAp(op)
        if (eventBus.contains(typeEvent::class.java, type.id)) {
            return typeEvent
        }

        val contentEvent = obj.toContentAp(type.contentGroup, op)
        if (eventBus.contains(contentEvent::class.java, type.contentGroup)) {
            return contentEvent
        }

        val defaultEvent = obj.toDefaultAp(op)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasApTrigger(
        player: Player,
        obj: Obj,
        type: UnpackedObjType,
        op: InteractionOp,
    ): Boolean = apTrigger(player, obj, type, op) != null

    private fun Obj.toOp(op: InteractionOp): ObjEvents.Op =
        when (op) {
            InteractionOp.Op1 -> ObjEvents.Op1(this)
            InteractionOp.Op2 -> ObjEvents.Op2(this)
            InteractionOp.Op3 -> ObjEvents.Op3(this)
            InteractionOp.Op4 -> ObjEvents.Op4(this)
            InteractionOp.Op5 -> ObjEvents.Op5(this)
        }

    private fun Obj.toContentOp(contentGroup: Int, op: InteractionOp): ObjContentEvents.Op =
        when (op) {
            InteractionOp.Op1 -> ObjContentEvents.Op1(this, contentGroup)
            InteractionOp.Op2 -> ObjContentEvents.Op2(this, contentGroup)
            InteractionOp.Op3 -> ObjContentEvents.Op3(this, contentGroup)
            InteractionOp.Op4 -> ObjContentEvents.Op4(this, contentGroup)
            InteractionOp.Op5 -> ObjContentEvents.Op5(this, contentGroup)
        }

    private fun Obj.toDefaultOp(op: InteractionOp): ObjDefaultEvents.Op =
        when (op) {
            InteractionOp.Op1 -> ObjDefaultEvents.Op1(this)
            InteractionOp.Op2 -> ObjDefaultEvents.Op2(this)
            InteractionOp.Op3 -> ObjDefaultEvents.Op3(this)
            InteractionOp.Op4 -> ObjDefaultEvents.Op4(this)
            InteractionOp.Op5 -> ObjDefaultEvents.Op5(this)
        }

    private fun Obj.toAp(op: InteractionOp): ObjEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> ObjEvents.Ap1(this)
            InteractionOp.Op2 -> ObjEvents.Ap2(this)
            InteractionOp.Op3 -> ObjEvents.Ap3(this)
            InteractionOp.Op4 -> ObjEvents.Ap4(this)
            InteractionOp.Op5 -> ObjEvents.Ap5(this)
        }

    private fun Obj.toContentAp(contentGroup: Int, op: InteractionOp): ObjContentEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> ObjContentEvents.Ap1(this, contentGroup)
            InteractionOp.Op2 -> ObjContentEvents.Ap2(this, contentGroup)
            InteractionOp.Op3 -> ObjContentEvents.Ap3(this, contentGroup)
            InteractionOp.Op4 -> ObjContentEvents.Ap4(this, contentGroup)
            InteractionOp.Op5 -> ObjContentEvents.Ap5(this, contentGroup)
        }

    private fun Obj.toDefaultAp(op: InteractionOp): ObjDefaultEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> ObjDefaultEvents.Ap1(this)
            InteractionOp.Op2 -> ObjDefaultEvents.Ap1(this)
            InteractionOp.Op3 -> ObjDefaultEvents.Ap1(this)
            InteractionOp.Op4 -> ObjDefaultEvents.Ap1(this)
            InteractionOp.Op5 -> ObjDefaultEvents.Ap1(this)
        }
}
