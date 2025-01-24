package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.buttons.IfButtonD
import org.rsmod.api.config.refs.objs
import org.rsmod.api.player.ui.IfButtonDrag
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.type.interf.isType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.isType
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

        // TODO: Verify `IfSetEvent` for dragging has been enabled for the components. This requires
        //  us to store the state of IfSetEvents for the player.

        // Client replaces empty obj ids with `6512`. To make life easier, we simply replace those
        // with null obj types as that's what associated scripts should treat them as.
        val selectedType = convertNullReplacement(objTypes[message.selectedObj])
        val targetType = convertNullReplacement(objTypes[message.targetObj])

        val buttonDrag =
            IfButtonDrag(
                player = player,
                selectedSlot = message.selectedSub,
                selectedObj = selectedType,
                targetSlot = message.targetSub,
                targetObj = targetType,
                selectedComponent = selectedComponent,
                targetComponent = targetComponent,
            )
        logger.debug { "IfButtonD: $message (event=$buttonDrag)" }
        eventBus.publish(buttonDrag)
    }

    private fun convertNullReplacement(type: UnpackedObjType?): UnpackedObjType? {
        return if (!type.isType(objs.null_item_placeholder)) {
            type
        } else {
            null
        }
    }
}
