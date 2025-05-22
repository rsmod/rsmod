package org.rsmod.api.player.interact

import jakarta.inject.Inject
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.api.player.events.interact.PlayerTEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionPlayerT
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.obj.ObjType

public class PlayerTInteractions @Inject constructor(private val eventBus: EventBus) {
    public fun interact(
        player: Player,
        target: Player,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ) {
        val opTrigger = hasOpTrigger(target, component, comsub, objType)
        val apTrigger = hasApTrigger(target, component, comsub, objType)
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
        player.facePlayer(target)
        player.interaction = interaction
        player.routeRequest = routeRequest
    }

    public fun opTrigger(
        target: Player,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): OpEvent? {
        val typeEvent = PlayerTEvents.Op(target, comsub, objType, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }
        return null
    }

    public fun hasOpTrigger(
        target: Player,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): Boolean = opTrigger(target, component, comsub, objType) != null

    public fun apTrigger(
        target: Player,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): ApEvent? {
        val typeEvent = PlayerTEvents.Ap(target, comsub, objType, component)
        if (eventBus.contains(typeEvent::class.java, typeEvent.id)) {
            return typeEvent
        }
        return null
    }

    public fun hasApTrigger(
        target: Player,
        component: ComponentType,
        comsub: Int,
        objType: ObjType?,
    ): Boolean = apTrigger(target, component, comsub, objType) != null
}
