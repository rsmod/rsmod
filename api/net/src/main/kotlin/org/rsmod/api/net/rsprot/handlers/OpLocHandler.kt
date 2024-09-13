package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.locs.OpLoc
import org.rsmod.api.interactions.LocInteractions
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.vars.ctrlMoveSpeed
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionLoc
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

    override fun handle(player: Player, message: OpLoc) {
        if (player.isDelayed) {
            return
        }
        val coords = CoordGrid(message.x, message.z, player.level)
        val loc = locRegistry.find(coords, id = message.id) ?: return
        val type = locTypes[message.id] ?: return
        val speed = if (message.controlKey) player.ctrlMoveSpeed() else null
        val boundLoc = BoundLocInfo(loc, type)
        val opTrigger = locInteractions.hasOpTrigger(player, boundLoc, type, message.op)
        val apTrigger = locInteractions.hasApTrigger(player, boundLoc, type, message.op)
        val interaction =
            InteractionLoc(
                target = boundLoc,
                opSlot = message.op,
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
        player.clearPendingAction(eventBus)
        player.resetFaceEntity()
        player.faceLoc(loc, type.width, type.length)
        player.interaction = interaction
        player.routeRequest = routeRequest
        player.tempMoveSpeed = speed
        logger.debug { "OpLoc: op=${message.op}, loc=$boundLoc type=$type" }
    }
}
