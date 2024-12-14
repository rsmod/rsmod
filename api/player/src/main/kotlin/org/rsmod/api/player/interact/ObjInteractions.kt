package org.rsmod.api.player.interact

import jakarta.inject.Inject
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.ObjContentEvents
import org.rsmod.api.player.events.interact.ObjDefaultEvents
import org.rsmod.api.player.events.interact.ObjEvents
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionObj
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.movement.RouteRequestCoord
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType

public class ObjInteractions
@Inject
constructor(private val objTypes: ObjTypeList, private val eventBus: EventBus) {
    public fun interact(
        player: Player,
        obj: Obj,
        op: InteractionOp,
        type: UnpackedObjType = objTypes[obj],
    ) {
        val opTrigger = hasOpTrigger(obj, op, type)
        val apTrigger = hasApTrigger(obj, op, type)
        val interaction =
            InteractionObj(
                target = obj,
                op = op,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest = RouteRequestCoord(obj.coords)
        player.clearPendingAction(eventBus)
        player.clearMapFlag()
        player.interaction = interaction
        player.routeRequest = routeRequest
    }

    public fun opTrigger(
        obj: Obj,
        op: InteractionOp,
        type: UnpackedObjType = objTypes[obj],
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
        obj: Obj,
        op: InteractionOp,
        type: UnpackedObjType = objTypes[obj],
    ): Boolean = opTrigger(obj, op, type) != null

    public fun apTrigger(
        obj: Obj,
        op: InteractionOp,
        type: UnpackedObjType = objTypes[obj],
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
        obj: Obj,
        op: InteractionOp,
        type: UnpackedObjType = objTypes[obj],
    ): Boolean = apTrigger(obj, op, type) != null

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
