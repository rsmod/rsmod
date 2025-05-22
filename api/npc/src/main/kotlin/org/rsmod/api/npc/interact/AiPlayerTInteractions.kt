package org.rsmod.api.npc.interact

import jakarta.inject.Inject
import org.rsmod.api.npc.events.interact.AiPlayerTContentEvents
import org.rsmod.api.npc.events.interact.AiPlayerTEvents
import org.rsmod.api.npc.events.interact.ApEvent
import org.rsmod.api.npc.events.interact.OpEvent
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionPlayerT
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.obj.ObjType

public class AiPlayerTInteractions @Inject constructor(private val eventBus: EventBus) {
    public fun interact(
        npc: Npc,
        target: Player,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ) {
        val opTrigger = hasOpTrigger(npc, target, component, comsub, objType)
        val apTrigger = hasApTrigger(npc, target, component, comsub, objType)
        val interaction =
            InteractionPlayerT(
                target = target,
                comsub = comsub,
                objType = objType,
                component = component,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest = RouteRequestPathingEntity(target.avatar)
        npc.interaction = interaction
        npc.routeRequest = routeRequest
    }

    public fun opTrigger(
        npc: Npc,
        target: Player,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): OpEvent? {
        val typeEvent = AiPlayerTEvents.Op(target, comsub, objType, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent =
            AiPlayerTContentEvents.Op(target, comsub, objType, component, npc.visType.contentGroup)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        return null
    }

    public fun hasOpTrigger(
        npc: Npc,
        target: Player,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): Boolean = opTrigger(npc, target, component, comsub, objType) != null

    public fun apTrigger(
        npc: Npc,
        target: Player,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): ApEvent? {
        val typeEvent = AiPlayerTEvents.Ap(target, comsub, objType, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent =
            AiPlayerTContentEvents.Ap(target, comsub, objType, component, npc.visType.contentGroup)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        return null
    }

    public fun hasApTrigger(
        npc: Npc,
        target: Player,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): Boolean = apTrigger(npc, target, component, comsub, objType) != null
}
