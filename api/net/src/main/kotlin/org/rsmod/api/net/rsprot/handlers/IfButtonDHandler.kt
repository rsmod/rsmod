package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.buttons.IfButtonD
import org.rsmod.api.player.ui.IfButtonDrag
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.type.interf.isType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.ui.Component

class IfButtonDHandler
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val interfaceTypes: InterfaceTypeList,
    private val eventBus: EventBus,
) : MessageHandler<IfButtonD> {
    private val logger = InlineLogger()

    override fun handle(player: Player, message: IfButtonD) {
        val selectedComponent = Component(message.selectedInterfaceId, message.selectedComponentId)
        val selectedInterface = interfaceTypes[selectedComponent]
        val targetComponent = Component(message.targetInterfaceId, message.targetComponentId)
        val targetInterface = interfaceTypes[targetComponent]
        val ui = player.ui

        val selectedOpened =
            ui.containsModal(selectedInterface) || ui.containsOverlay(selectedInterface)
        if (!selectedOpened) {
            logger.debug { "Selected interface is not opened: message=$message, player=$player" }
            return
        }

        // No need to verify again if dragging objs on the same interface.
        val skipTargetVerification = selectedInterface.isType(targetInterface)
        if (!skipTargetVerification) {
            val targetOpened =
                ui.containsModal(targetInterface) || ui.containsOverlay(targetInterface)
            if (!targetOpened) {
                logger.debug { "Target interface is not opened: message=$message, player=$player" }
                return
            }
        }

        // TODO: Verify `IfSetEvent` for dragging has been enabled components. This requires us to
        //  store the state of IfSetEvents for the player.

        val selectedType = objTypes[message.selectedObj]
        val selectedSlot = message.selectedSub
        val targetType = objTypes[message.targetObj]
        val targetSlot = message.targetSub

        val buttonDrag =
            IfButtonDrag(
                player = player,
                selectedSlot = selectedSlot,
                selectedObj = selectedType,
                targetSlot = targetSlot,
                targetObj = targetType,
                selectedComponent = selectedComponent,
                targetComponent = targetComponent,
            )
        logger.debug { "IfButtonD: $message (event=$buttonDrag)" }
        eventBus.publish(buttonDrag)
    }
}
