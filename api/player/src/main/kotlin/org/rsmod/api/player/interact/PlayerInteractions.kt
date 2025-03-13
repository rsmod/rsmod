package org.rsmod.api.player.interact

import jakarta.inject.Inject
import org.rsmod.api.player.events.interact.ApEvent
import org.rsmod.api.player.events.interact.OpEvent
import org.rsmod.api.player.events.interact.PlayerEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.interact.InteractionPlayerOp
import org.rsmod.game.movement.RouteRequestPathingEntity

public class PlayerInteractions @Inject constructor(private val eventBus: EventBus) {
    public fun interact(player: Player, target: Player, op: InteractionOp) {
        val opTrigger = hasOpTrigger(target, op)
        val apTrigger = hasApTrigger(target, op)
        val interaction =
            InteractionPlayerOp(
                target = target,
                op = op,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest = RouteRequestPathingEntity(target.avatar)
        player.facePlayer(target)
        player.interaction = interaction
        player.routeRequest = routeRequest
    }

    public fun opTrigger(target: Player, op: InteractionOp): OpEvent? {
        val event = target.toOp(op)
        if (eventBus.contains(event::class.java, event.id)) {
            return event
        }
        return null
    }

    public fun hasOpTrigger(target: Player, op: InteractionOp): Boolean =
        opTrigger(target, op) != null

    public fun apTrigger(target: Player, op: InteractionOp): ApEvent? {
        val event = target.toAp(op)
        if (eventBus.contains(event::class.java, event.id)) {
            return event
        }
        return null
    }

    public fun hasApTrigger(target: Player, op: InteractionOp): Boolean =
        apTrigger(target, op) != null

    private fun Player.toOp(op: InteractionOp): PlayerEvents.Op =
        when (op) {
            InteractionOp.Op1 -> PlayerEvents.Op1(this)
            InteractionOp.Op2 -> PlayerEvents.Op2(this)
            InteractionOp.Op3 -> PlayerEvents.Op3(this)
            InteractionOp.Op4 -> PlayerEvents.Op4(this)
            InteractionOp.Op5 -> PlayerEvents.Op5(this)
        }

    private fun Player.toAp(op: InteractionOp): PlayerEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> PlayerEvents.Ap1(this)
            InteractionOp.Op2 -> PlayerEvents.Ap2(this)
            InteractionOp.Op3 -> PlayerEvents.Ap3(this)
            InteractionOp.Op4 -> PlayerEvents.Ap4(this)
            InteractionOp.Op5 -> PlayerEvents.Ap5(this)
        }
}
