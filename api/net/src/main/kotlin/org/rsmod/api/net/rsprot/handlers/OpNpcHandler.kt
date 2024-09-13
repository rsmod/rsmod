package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.npcs.OpNpc
import org.rsmod.api.interactions.NpcInteractions
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.vars.ctrlMoveSpeed
import org.rsmod.events.EventBus
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionNpc
import org.rsmod.game.movement.RouteRequestPathingEntity

class OpNpcHandler
@Inject
constructor(
    private val eventBus: EventBus,
    val npcList: NpcList,
    private val npcInteractions: NpcInteractions,
) : MessageHandler<OpNpc> {
    private val logger = InlineLogger()

    override fun handle(player: Player, message: OpNpc) {
        if (player.isDelayed) {
            player.clearMapFlag()
            return
        }
        val npc = npcList[message.index] ?: return
        val speed = if (message.controlKey) player.ctrlMoveSpeed() else null
        val opTrigger = npcInteractions.hasOpTrigger(player, npc, message.op)
        val apTrigger = npcInteractions.hasApTrigger(player, npc, message.op)
        val interaction =
            InteractionNpc(
                target = npc,
                opSlot = message.op,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest = RouteRequestPathingEntity(npc.avatar)
        player.clearPendingAction(eventBus)
        player.faceNpc(npc)
        player.interaction = interaction
        player.routeRequest = routeRequest
        player.tempMoveSpeed = speed
        logger.debug { "OpNpc: op=${message.op}, npc=$npc" }
    }
}
