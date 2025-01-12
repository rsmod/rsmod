package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.locs.OpLoc
import org.rsmod.api.player.interact.LocInteractions
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.vars.ctrlMoveSpeed
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionLoc
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.movement.RouteRequestLoc
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.map.CoordGrid

class OpLocHandler
@Inject
constructor(
    private val eventBus: EventBus,
    private val locTypes: LocTypeList,
    private val locRegistry: LocRegistry,
    private val locInteractions: LocInteractions,
) : MessageHandler<OpLoc> {
    private val logger = InlineLogger()

    private val OpLoc.interactionOp: InteractionOp
        get() =
            when (op) {
                1 -> InteractionOp.Op1
                2 -> InteractionOp.Op2
                3 -> InteractionOp.Op3
                4 -> InteractionOp.Op4
                5 -> InteractionOp.Op5
                else -> throw NotImplementedError("Unhandled `op` conversion: $this")
            }

    override fun handle(player: Player, message: OpLoc) {
        if (player.isDelayed) {
            return
        }
        val coords = CoordGrid(message.x, message.z, player.level)
        val loc = locRegistry.findExact(coords, id = message.id) ?: return
        val type = locTypes[message.id] ?: return
        val speed = if (message.controlKey) player.ctrlMoveSpeed() else null
        val boundLoc = BoundLocInfo(loc, type)
        val opTrigger = locInteractions.hasOpTrigger(player, boundLoc, message.interactionOp, type)
        val apTrigger = locInteractions.hasApTrigger(player, boundLoc, message.interactionOp, type)
        val interaction =
            InteractionLoc(
                target = boundLoc,
                op = message.interactionOp,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest =
            RouteRequestLoc(
                destination = coords,
                width = type.width,
                length = type.length,
                shape = loc.entity.shape,
                angle = loc.entity.angle,
                forceApproachFlags = type.forceApproachFlags,
            )
        if (!locInteractions.hasOp(boundLoc, type, player.vars, message.interactionOp)) {
            logger.debug { "OpLoc blocked due to op: op=${message.op}, loc=$boundLoc type=$type" }
            return
        }
        player.clearPendingAction(eventBus)
        player.resetFaceEntity()
        player.faceLoc(loc, type.width, type.length)
        player.interaction = interaction
        player.routeRequest = routeRequest
        player.tempMoveSpeed = speed
        logger.debug { "OpLoc: op=${message.op}, loc=$boundLoc type=$type" }
    }
}
