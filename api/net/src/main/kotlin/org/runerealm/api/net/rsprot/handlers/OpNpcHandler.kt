package org.runerealm.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.npcs.OpNpc
import org.runerealm.api.player.interact.NpcInteractions
import org.runerealm.api.player.output.clearMapFlag
import org.runerealm.api.player.protect.clearPendingAction
import org.runerealm.api.player.vars.ctrlMoveSpeed
import org.runerealm.events.EventBus
import org.runerealm.game.entity.NpcList
import org.runerealm.game.entity.Player
import org.runerealm.game.interact.InteractionNpcOp
import org.runerealm.game.interact.InteractionOp
import org.runerealm.game.movement.RouteRequestPathingEntity

class OpNpcHandler
@Inject
constructor(
    private val eventBus: EventBus,
    private val npcList: NpcList,
    private val npcInteractions: NpcInteractions,
) : MessageHandler<OpNpc> {
    private val logger = InlineLogger()

    private val OpNpc.interactionOp: InteractionOp
        get() =
            when (op) {
                1 -> InteractionOp.Op1
                2 -> InteractionOp.Op2
                3 -> InteractionOp.Op3
                4 -> InteractionOp.Op4
                5 -> InteractionOp.Op5
                else -> throw NotImplementedError("Unhandled `op` conversion: $this")
            }

    override fun handle(player: Player, message: OpNpc) {
        if (player.isDelayed) {
            player.clearMapFlag()
            return
        }

        val npc = npcList[message.index]
        if (npc == null) {
            player.clearMapFlag()
            player.clearPendingAction(eventBus)
            return
        }

        val speed = if (message.controlKey) player.ctrlMoveSpeed() else null
        val opTrigger = npcInteractions.hasOpTrigger(player, npc, message.interactionOp)
        val apTrigger = npcInteractions.hasApTrigger(player, npc, message.interactionOp)
        val interaction =
            InteractionNpcOp(
                target = npc,
                op = message.interactionOp,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest = RouteRequestPathingEntity(npc.avatar, clientRequest = true)

        if (npc.isDelayed) {
            return
        }

        if (!npcInteractions.hasOp(npc, player.vars, message.interactionOp)) {
            logger.debug { "OpNpc invalid op blocked: op=${message.op}, npc=$npc" }
            return
        }

        player.clearPendingAction(eventBus)
        player.faceNpc(npc)
        player.interaction = interaction
        player.routeRequest = routeRequest
        player.tempMoveSpeed = speed
        logger.debug { "OpNpc: op=${message.op}, npc=$npc" }
    }
}
