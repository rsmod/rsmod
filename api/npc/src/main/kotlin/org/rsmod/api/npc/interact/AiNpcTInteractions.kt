package org.rsmod.api.npc.interact

import jakarta.inject.Inject
import org.rsmod.api.npc.events.interact.AiNpcTContentEvents
import org.rsmod.api.npc.events.interact.AiNpcTDefaultEvents
import org.rsmod.api.npc.events.interact.AiNpcTEvents
import org.rsmod.api.npc.events.interact.ApEvent
import org.rsmod.api.npc.events.interact.OpEvent
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.interact.InteractionNpcT
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType

public class AiNpcTInteractions
@Inject
constructor(private val npcTypes: NpcTypeList, private val eventBus: EventBus) {
    public fun interact(
        npc: Npc,
        target: Npc,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ) {
        val opTrigger = hasOpTrigger(target, component, comsub, objType)
        val apTrigger = hasApTrigger(target, component, comsub, objType)
        val interaction =
            InteractionNpcT(
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
        target: Npc,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
        type: UnpackedNpcType = target.visType,
    ): OpEvent? {
        val typeEvent = AiNpcTEvents.Op(target, comsub, objType, type, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent =
            AiNpcTContentEvents.Op(target, comsub, objType, component, type.contentGroup)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        val defaultEvent = AiNpcTDefaultEvents.Op(target, comsub, objType, type, component)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasOpTrigger(
        target: Npc,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): Boolean = opTrigger(target, component, comsub, objType) != null

    public fun apTrigger(
        target: Npc,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
        type: UnpackedNpcType = target.visType,
    ): ApEvent? {
        val typeEvent = AiNpcTEvents.Ap(target, comsub, objType, type, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent =
            AiNpcTContentEvents.Ap(target, comsub, objType, component, type.contentGroup)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        val defaultEvent = AiNpcTDefaultEvents.Ap(target, comsub, objType, type, component)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasApTrigger(
        target: Npc,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): Boolean = apTrigger(target, component, comsub, objType) != null
}
