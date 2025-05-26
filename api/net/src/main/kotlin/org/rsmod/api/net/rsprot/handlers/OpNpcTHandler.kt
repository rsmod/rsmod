package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.npcs.OpNpcT
import org.rsmod.api.net.rsprot.player.InterfaceEvents
import org.rsmod.api.player.interact.NpcTInteractions
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.protect.clearPendingAction
import org.rsmod.api.player.vars.ctrlMoveSpeed
import org.rsmod.events.EventBus
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.Player
import org.rsmod.game.interact.InteractionNpcT
import org.rsmod.game.movement.RouteRequestPathingEntity
import org.rsmod.game.type.comp.ComponentTypeList
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.ui.Component

class OpNpcTHandler
@Inject
constructor(
    private val eventBus: EventBus,
    private val npcList: NpcList,
    private val objTypes: ObjTypeList,
    private val componentTypes: ComponentTypeList,
    private val interfaceTypes: InterfaceTypeList,
    private val npcInteractions: NpcTInteractions,
) : MessageHandler<OpNpcT> {
    private val logger = InlineLogger()

    private val OpNpcT.asComponent: Component
        get() = Component(selectedInterfaceId, selectedComponentId)

    override fun handle(player: Player, message: OpNpcT) {
        if (player.isDelayed) {
            player.clearMapFlag()
            return
        }

        val npc = npcList[message.index]

        if (npc == null || npc.isDelayed) {
            player.clearMapFlag()
            player.clearPendingAction(eventBus)
            return
        }

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
            InterfaceEvents.isEnabled(player.ui, componentType, comsub, IfEvent.TgtNpc)
        if (!targetEnabled) {
            player.clearMapFlag()
            player.clearPendingAction(eventBus)
            return
        }

        val speed = if (message.controlKey) player.ctrlMoveSpeed() else null
        val opTrigger = npcInteractions.hasOpTrigger(player, npc, componentType, comsub, objType)
        val apTrigger = npcInteractions.hasApTrigger(player, npc, componentType, comsub, objType)
        val interaction =
            InteractionNpcT(
                target = npc,
                comsub = comsub,
                objType = objType,
                component = componentType,
                hasOpTrigger = opTrigger,
                hasApTrigger = apTrigger,
            )
        val routeRequest = RouteRequestPathingEntity(npc.avatar, clientRequest = true)
        player.clearPendingAction(eventBus)
        player.faceNpc(npc)
        player.interaction = interaction
        player.routeRequest = routeRequest
        player.tempMoveSpeed = speed
        logger.debug { "OpNpcT: npc=$npc, comsub=$comsub, component=$componentType, obj=$objType" }
    }
}
