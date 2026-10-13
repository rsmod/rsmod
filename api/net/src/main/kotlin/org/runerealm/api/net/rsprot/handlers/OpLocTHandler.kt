package org.runerealm.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.locs.OpLocT
import org.runerealm.api.net.rsprot.player.InterfaceEvents
import org.runerealm.api.player.interact.LocTInteractions
import org.runerealm.api.player.output.clearMapFlag
import org.runerealm.api.player.protect.clearPendingAction
import org.runerealm.api.player.vars.ctrlMoveSpeed
import org.runerealm.api.registry.loc.LocRegistry
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.interact.InteractionLocT
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.movement.RouteRequestLoc
import org.runerealm.game.type.comp.ComponentTypeList
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.game.type.interf.InterfaceTypeList
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.ui.Component
import org.runerealm.map.CoordGrid

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
        val loc = locRegistry.findType(coords, message.id)
        if (loc == null) {
            player.clearMapFlag()
            player.clearPendingAction(eventBus)
            return
        }
        val type = locTypes[message.id] ?: return
        val interfaceType = interfaceTypes[message.asComponent]
        val componentType = componentTypes[message.asComponent]
        val objType = message.selectedObj.takeIf { it != -1 }?.let(objTypes::get)

        val isValidInterface =
            player.ui.containsOverlay(interfaceType) || player.ui.containsModal(interfaceType)
        if (!isValidInterface) {
            player.clearMapFlag()
            player.clearPendingAction(eventBus)
            return
        }

        val comsub = message.selectedSub

        val targetEnabled =
            InterfaceEvents.isEnabled(player.ui, componentType, comsub, IfEvent.TgtLoc)
        if (!targetEnabled) {
            player.clearMapFlag()
            player.clearPendingAction(eventBus)
            return
        }

        val speed = if (message.controlKey) player.ctrlMoveSpeed() else null
        val boundLoc = BoundLocInfo(loc, type)
        val opTrigger =
            locInteractions.hasOpTrigger(player, boundLoc, type, objType, componentType, comsub)
        val apTrigger =
            locInteractions.hasApTrigger(player, boundLoc, type, objType, componentType, comsub)
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
                clientRequest = true,
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
