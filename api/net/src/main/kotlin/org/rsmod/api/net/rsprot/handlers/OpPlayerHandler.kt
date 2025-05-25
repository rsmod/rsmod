package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.players.OpPlayer
import org.rsmod.api.player.interact.PlayerInteractions
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.vars.ctrlMoveSpeed
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.interact.InteractionPlayerOp
import org.rsmod.game.movement.RouteRequestPathingEntity

class OpPlayerHandler
@Inject
constructor(
    private val eventBus: EventBus,
    private val playerList: PlayerList,
    private val playerInteractions: PlayerInteractions,
) : MessageHandler<OpPlayer> {
    private val logger = InlineLogger()

    private val OpPlayer.interactionOp: InteractionOp
        get() =
            when (op) {
                1 -> InteractionOp.Op1
                2 -> InteractionOp.Op2
                3 -> InteractionOp.Op3
                4 -> InteractionOp.Op4
                5 -> InteractionOp.Op5
                else -> throw NotImplementedError("Unhandled `op` conversion: $this")
            }

    override fun handle(player: Player, message: OpPlayer) {
        if (player.isDelayed) {
            player.clearMapFlag()
            return
        }

        val target = playerList[message.index]
        if (target == null) {
            player.clearMapFlag()
            player.clearPendingAction(eventBus)
            return
        }

        val speed = if (message.controlKey) player.ctrlMoveSpeed() else null
        val opTrigger = playerInteractions.hasOpTrigger(target, message.interactionOp)
        val apTrigger = playerInteractions.hasApTrigger(target, message.interactionOp)
        val interaction =
            InteractionPlayerOp(
                target = target,
                op = message.interactionOp,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest = RouteRequestPathingEntity(target.avatar, clientRequest = true)

        player.clearPendingAction(eventBus)
        player.facePlayer(target)
        player.interaction = interaction
        player.routeRequest = routeRequest
        player.tempMoveSpeed = speed
        logger.debug { "OpPlayer: op=${message.op}, target=$target" }
    }
}
