package org.runerealm.api.npc.interact

import jakarta.inject.Inject
import org.runerealm.api.npc.events.interact.AiPlayerTContentEvents
import org.runerealm.api.npc.events.interact.AiPlayerTEvents
import org.runerealm.api.npc.events.interact.ApEvent
import org.runerealm.api.npc.events.interact.OpEvent
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.interact.InteractionPlayerT
import org.runerealm.game.movement.RouteRequestPathingEntity
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.obj.ObjType

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
