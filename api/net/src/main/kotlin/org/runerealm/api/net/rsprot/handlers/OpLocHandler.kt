package org.runerealm.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.locs.OpLoc
import org.runerealm.api.player.interact.LocInteractions
import org.runerealm.api.player.output.clearMapFlag
import org.runerealm.api.player.protect.clearPendingAction
import org.runerealm.api.player.vars.ctrlMoveSpeed
import org.runerealm.api.registry.loc.LocRegistry
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.interact.InteractionLocOp
import org.runerealm.game.interact.InteractionOp
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.movement.RouteRequestLoc
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.map.CoordGrid

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
        val loc = locRegistry.findType(coords, message.id)
        if (loc == null) {
            player.clearMapFlag()
            return
        }
        val type = locTypes[message.id] ?: return
        val speed = if (message.controlKey) player.ctrlMoveSpeed() else null
        val boundLoc = BoundLocInfo(loc, type)
        val opTrigger = locInteractions.hasOpTrigger(player, boundLoc, message.interactionOp, type)
        val apTrigger = locInteractions.hasApTrigger(player, boundLoc, message.interactionOp, type)
        val interaction =
            InteractionLocOp(
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
                clientRequest = true,
            )
        if (!locInteractions.hasOp(boundLoc, type, player.vars, message.interactionOp)) {
            logger.debug { "OpLoc invalid op blocked: op=${message.op}, loc=$boundLoc type=$type" }
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
