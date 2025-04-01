package org.rsmod.api.npc.interact

import jakarta.inject.Inject
import org.rsmod.api.npc.events.interact.AiNpcContentEvents
import org.rsmod.api.npc.events.interact.AiNpcDefaultEvents
import org.rsmod.api.npc.events.interact.AiNpcEvents
import org.rsmod.api.npc.events.interact.AiNpcUnimplementedEvents
import org.rsmod.api.npc.events.interact.ApEvent
import org.rsmod.api.npc.events.interact.OpEvent
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.interact.InteractionNpcOp
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType

public class AiNpcInteractions
@Inject
constructor(private val npcTypes: NpcTypeList, private val eventBus: EventBus) {
    public fun interactOp(npc: Npc, target: Npc, op: InteractionOp) {
        val opTrigger = hasOpTrigger(target, op)
        val interaction =
            InteractionNpcOp(
                target = target,
                op = op,
                hasOpTrigger = opTrigger,
                hasApTrigger = false,
            )
        val routeRequest = RouteRequestPathingEntity(target.avatar)
        npc.interaction = interaction
        npc.routeRequest = routeRequest
    }

    public fun interactAp(npc: Npc, target: Npc, op: InteractionOp) {
        val apRange = npc.visType.attackRange
        val interaction =
            InteractionNpcOp(
                target = target,
                op = op,
                hasOpTrigger = false,
                hasApTrigger = true,
                startApRange = apRange,
            )
        val routeRequest = RouteRequestPathingEntity(target.avatar)
        npc.interaction = interaction
        npc.routeRequest = routeRequest
    }

    public fun opTrigger(
        target: Npc,
        op: InteractionOp,
        type: UnpackedNpcType = target.visType,
    ): OpEvent? {
        val typeEvent = target.toOp(op)
        if (eventBus.contains(typeEvent::class.java, type.id)) {
            return typeEvent
        }

        val contentEvent = target.toContentOp(type.contentGroup, op)
        if (eventBus.contains(contentEvent::class.java, type.contentGroup)) {
            return contentEvent
        }

        val unimplEvent = target.toUnimplementedOp(op)
        if (eventBus.contains(unimplEvent::class.java, type.id)) {
            return unimplEvent
        }

        val defaultEvent = target.toDefaultOp(op)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasOpTrigger(target: Npc, op: InteractionOp): Boolean = opTrigger(target, op) != null

    public fun apTrigger(
        target: Npc,
        op: InteractionOp,
        type: UnpackedNpcType = target.visType,
    ): ApEvent? {
        val typeEvent = target.toAp(op)
        if (eventBus.contains(typeEvent::class.java, type.id)) {
            return typeEvent
        }

        val contentEvent = target.toContentAp(type.contentGroup, op)
        if (eventBus.contains(contentEvent::class.java, type.contentGroup)) {
            return contentEvent
        }

        val defaultEvent = target.toDefaultAp(op)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    private fun Npc.toOp(op: InteractionOp): AiNpcEvents.Op =
        when (op) {
            InteractionOp.Op1 -> AiNpcEvents.Op1(this)
            InteractionOp.Op2 -> AiNpcEvents.Op2(this)
            InteractionOp.Op3 -> AiNpcEvents.Op3(this)
            InteractionOp.Op4 -> AiNpcEvents.Op4(this)
            InteractionOp.Op5 -> AiNpcEvents.Op5(this)
        }

    private fun Npc.toContentOp(contentGroup: Int, op: InteractionOp): AiNpcContentEvents.Op =
        when (op) {
            InteractionOp.Op1 -> AiNpcContentEvents.Op1(this, contentGroup)
            InteractionOp.Op2 -> AiNpcContentEvents.Op2(this, contentGroup)
            InteractionOp.Op3 -> AiNpcContentEvents.Op3(this, contentGroup)
            InteractionOp.Op4 -> AiNpcContentEvents.Op4(this, contentGroup)
            InteractionOp.Op5 -> AiNpcContentEvents.Op5(this, contentGroup)
        }

    private fun Npc.toUnimplementedOp(op: InteractionOp): AiNpcUnimplementedEvents.Op =
        when (op) {
            InteractionOp.Op1 -> AiNpcUnimplementedEvents.Op1(this)
            InteractionOp.Op2 -> AiNpcUnimplementedEvents.Op2(this)
            InteractionOp.Op3 -> AiNpcUnimplementedEvents.Op3(this)
            InteractionOp.Op4 -> AiNpcUnimplementedEvents.Op4(this)
            InteractionOp.Op5 -> AiNpcUnimplementedEvents.Op5(this)
        }

    private fun Npc.toDefaultOp(op: InteractionOp): AiNpcDefaultEvents.Op =
        when (op) {
            InteractionOp.Op1 -> AiNpcDefaultEvents.Op1(this)
            InteractionOp.Op2 -> AiNpcDefaultEvents.Op2(this)
            InteractionOp.Op3 -> AiNpcDefaultEvents.Op3(this)
            InteractionOp.Op4 -> AiNpcDefaultEvents.Op4(this)
            InteractionOp.Op5 -> AiNpcDefaultEvents.Op5(this)
        }

    private fun Npc.toAp(op: InteractionOp): AiNpcEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> AiNpcEvents.Ap1(this)
            InteractionOp.Op2 -> AiNpcEvents.Ap2(this)
            InteractionOp.Op3 -> AiNpcEvents.Ap3(this)
            InteractionOp.Op4 -> AiNpcEvents.Ap4(this)
            InteractionOp.Op5 -> AiNpcEvents.Ap5(this)
        }

    private fun Npc.toContentAp(contentGroup: Int, op: InteractionOp): AiNpcContentEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> AiNpcContentEvents.Ap1(this, contentGroup)
            InteractionOp.Op2 -> AiNpcContentEvents.Ap2(this, contentGroup)
            InteractionOp.Op3 -> AiNpcContentEvents.Ap3(this, contentGroup)
            InteractionOp.Op4 -> AiNpcContentEvents.Ap4(this, contentGroup)
            InteractionOp.Op5 -> AiNpcContentEvents.Ap5(this, contentGroup)
        }

    private fun Npc.toDefaultAp(op: InteractionOp): AiNpcDefaultEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> AiNpcDefaultEvents.Ap1(this)
            InteractionOp.Op2 -> AiNpcDefaultEvents.Ap2(this)
            InteractionOp.Op3 -> AiNpcDefaultEvents.Ap3(this)
            InteractionOp.Op4 -> AiNpcDefaultEvents.Ap4(this)
            InteractionOp.Op5 -> AiNpcDefaultEvents.Ap5(this)
        }

    public fun hasOp(target: Npc, op: InteractionOp): Boolean = target.visType.hasOp(op)
}
