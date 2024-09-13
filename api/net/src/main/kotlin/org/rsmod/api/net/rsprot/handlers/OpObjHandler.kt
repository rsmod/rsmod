package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.objs.OpObj
import org.rsmod.api.interactions.ObjInteractions
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.vars.ctrlMoveSpeed
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionObj
import org.rsmod.game.movement.RouteRequestCoord
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.map.CoordGrid

class OpObjHandler
@Inject
constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val objRegistry: ObjRegistry,
    private val objInteractions: ObjInteractions,
) : MessageHandler<OpObj> {
    private val logger = InlineLogger()

    override fun handle(player: Player, message: OpObj) {
        if (player.isDelayed) {
            return
        }
        val coords = CoordGrid(message.x, message.z, player.level)
        val obj = findObj(player, coords, message.id) ?: return
        val type = objTypes[obj.type] ?: return
        val speed = if (message.controlKey) player.ctrlMoveSpeed() else null
        val opTrigger = objInteractions.hasOpTrigger(player, obj, type, message.op)
        val apTrigger = objInteractions.hasApTrigger(player, obj, type, message.op)
        val interaction =
            InteractionObj(
                target = obj,
                opSlot = message.op,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest = RouteRequestCoord(coords)
        player.clearPendingAction(eventBus)
        player.resetFaceEntity()
        player.faceSquare(coords, targetWidth = 1, targetLength = 1)
        player.interaction = interaction
        player.routeRequest = routeRequest
        player.tempMoveSpeed = speed
        logger.debug { "OpObj: op=${message.op}, obj=$obj, type=$type" }
    }

    private fun findObj(player: Player, coords: CoordGrid, type: Int): Obj? =
        objRegistry.findAll(coords).firstOrNull { it.type == type && it.isVisibleTo(player) }
}
