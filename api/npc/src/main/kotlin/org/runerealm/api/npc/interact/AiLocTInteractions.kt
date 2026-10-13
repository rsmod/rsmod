package org.runerealm.api.npc.interact

import jakarta.inject.Inject
import org.runerealm.api.npc.events.interact.AiLocTContentEvents
import org.runerealm.api.npc.events.interact.AiLocTDefaultEvents
import org.runerealm.api.npc.events.interact.AiLocTEvents
import org.runerealm.api.npc.events.interact.ApEvent
import org.runerealm.api.npc.events.interact.OpEvent
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Npc
import org.runerealm.game.interact.InteractionLocT
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.movement.RouteRequestLoc
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.game.type.obj.ObjType

public class AiLocTInteractions
@Inject
constructor(private val locTypes: LocTypeList, private val eventBus: EventBus) {
    public fun interact(
        npc: Npc,
        loc: BoundLocInfo,
        type: UnpackedLocType,
        objType: ObjType?,
        component: ComponentType,
        comsub: Int,
    ) {
        val opTrigger = hasOpTrigger(loc, type, objType, component, comsub)
        val apTrigger = hasApTrigger(loc, type, objType, component, comsub)
        val interaction =
            InteractionLocT(
                target = loc,
                comsub = comsub,
                objType = objType,
                component = component,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest =
            RouteRequestLoc(
                destination = loc.coords,
                width = type.width,
                length = type.length,
                shape = loc.entity.shape,
                angle = loc.entity.angle,
                forceApproachFlags = type.forceApproachFlags,
            )
        npc.interaction = interaction
        npc.routeRequest = routeRequest
    }

    public fun opTrigger(
        loc: BoundLocInfo,
        objType: ObjType?,
        component: ComponentType,
        comsub: Int,
        type: UnpackedLocType = locTypes[loc],
    ): OpEvent? {
        val typeEvent = AiLocTEvents.Op(loc, type, objType, comsub, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent = AiLocTContentEvents.Op(loc, type, objType, comsub, component)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        val defaultEvent = AiLocTDefaultEvents.Op(loc, type, objType, comsub, component)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasOpTrigger(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        objType: ObjType?,
        component: ComponentType,
        comsub: Int,
    ): Boolean = opTrigger(loc, objType, component, comsub, type) != null

    public fun apTrigger(
        loc: BoundLocInfo,
        objType: ObjType?,
        component: ComponentType,
        comsub: Int,
        type: UnpackedLocType = locTypes[loc],
    ): ApEvent? {
        val typeEvent = AiLocTEvents.Ap(loc, type, objType, comsub, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }

        val contentEvent = AiLocTContentEvents.Ap(loc, type, objType, comsub, component)
        if (eventBus.contains(contentEvent::class.java, contentEvent.id)) {
            return contentEvent
        }

        val defaultEvent = AiLocTDefaultEvents.Ap(loc, type, objType, comsub, component)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasApTrigger(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        objType: ObjType?,
        component: ComponentType,
        comsub: Int,
    ): Boolean = apTrigger(loc, objType, component, comsub, type) != null
}
