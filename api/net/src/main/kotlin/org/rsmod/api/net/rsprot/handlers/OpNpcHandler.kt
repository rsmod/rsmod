package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.npcs.OpNpc
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.vars.ctrlMoveSpeed
import org.rsmod.events.EventBus
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionNpc
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.movement.RouteRequestPathingEntity

class OpNpcHandler
@Inject
constructor(
    private val eventBus: EventBus,
    val npcList: NpcList,
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
        val npc = npcList[message.index] ?: return
        val speed = if (message.controlKey) player.ctrlMoveSpeed() else null
        val opTrigger = npcInteractions.hasOpTrigger(player, npc, message.interactionOp)
        val apTrigger = npcInteractions.hasApTrigger(player, npc, message.interactionOp)
        val interaction =
            InteractionNpc(
                target = npc,
                op = message.interactionOp,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest = RouteRequestPathingEntity(npc.avatar)
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
