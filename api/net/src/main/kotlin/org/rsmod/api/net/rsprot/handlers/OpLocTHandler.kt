package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.locs.OpLocT
import org.rsmod.api.player.interact.LocTInteractions
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.vars.ctrlMoveSpeed
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionLocT
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.movement.RouteRequestLoc
import org.rsmod.game.type.comp.ComponentTypeList
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.type.loc.LocTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.ui.Component
import org.rsmod.map.CoordGrid

class OpLocTHandler
@Inject
constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val locTypes: LocTypeList,
    private val locRegistry: LocRegistry,
    private val componentTypes: ComponentTypeList,
    private val interfaceTypes: InterfaceTypeList,
    private val locInteractions: LocTInteractions,
) : MessageHandler<OpLocT> {
    private val logger = InlineLogger()

    private val OpLocT.asComponent: Component
        get() = Component(selectedInterfaceId, selectedComponentId)

    override fun handle(player: Player, message: OpLocT) {
        if (player.isDelayed) {
            return
        }

        val coords = CoordGrid(message.x, message.z, player.level)
        val loc = locRegistry.findType(coords, message.id) ?: return
        val type = locTypes[message.id] ?: return
        val interfaceType = interfaceTypes[message.asComponent]
        val componentType = componentTypes[message.asComponent]
        val objType = message.selectedObj.takeIf { it != -1 }?.let(objTypes::get)

        val isValidInterface =
            player.ui.containsOverlay(interfaceType) || player.ui.containsModal(interfaceType)
        if (!isValidInterface) {
            return
        }

        // TODO: Once `if_setevent`s are tracked properly, we can ensure the component has the
        // appropriate ifevent set for targeting.

        val comsub = message.selectedSub
        val speed = if (message.controlKey) player.ctrlMoveSpeed() else null
        val boundLoc = BoundLocInfo(loc, type)
        val opTrigger =
            locInteractions.hasOpTrigger(player, boundLoc, componentType, comsub, objType, type)
        val apTrigger =
            locInteractions.hasApTrigger(player, boundLoc, componentType, comsub, objType, type)
        val interaction =
            InteractionLocT(
                target = boundLoc,
                comsub = comsub,
                objType = objType,
                component = componentType,
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
        logger.debug {
            "OpLocT: loc=$boundLoc, type=$type, comsub=$comsub, " +
                "component=$componentType, obj=$objType"
        }
    }
}
