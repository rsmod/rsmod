package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.buttons.IfButtonT
import org.rsmod.annotations.InternalApi
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.IfModalButtonT
import org.rsmod.api.player.ui.IfOverlayButtonT
import org.rsmod.api.player.ui.ifCloseInputDialog
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.type.interf.isType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.ui.Component

class IfButtonTHandler
@Inject
constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val interfaceTypes: InterfaceTypeList,
    private val protectedAccess: ProtectedAccessLauncher,
) : MessageHandler<IfButtonT> {
    private val logger = InlineLogger()

    @OptIn(InternalApi::class)
    override fun handle(player: Player, message: IfButtonT) {
        val selectedComponent = Component(message.selectedInterfaceId, message.selectedComponentId)
        val selectedInterface = interfaceTypes[selectedComponent]
        val targetComponent = Component(message.targetInterfaceId, message.targetComponentId)
        val targetInterface = interfaceTypes[targetComponent]
        val ui = player.ui

        val isSelectedOpenedModal = ui.containsModal(selectedInterface)
        val isSelectedOpened = isSelectedOpenedModal || ui.containsOverlay(selectedInterface)
        if (!isSelectedOpened) {
            logger.debug { "Selected interface is not open: message=$message, player=$player" }
            return
        }

        // No need to verify again if both objs belong to the same interface.
        val skipTargetVerification = selectedInterface.isType(targetInterface)
        if (!skipTargetVerification) {
            val isTargetOpenedModal = ui.containsModal(targetInterface)
            val targetOpened = isTargetOpenedModal || ui.containsOverlay(targetInterface)
            if (!targetOpened) {
                logger.debug { "Target interface is not open: message=$message, player=$player" }
                return
            }
        }

        // TODO: Verify `IfSetEvent` for the respective targeting has been enabled for the
        //  components. This requires us to store the state of IfSetEvents for the player.

        val selectedObjType = objTypes[message.selectedObj]
        val targetObjType = objTypes[message.targetObj]

        val isSelectedOverlay = !isSelectedOpenedModal
        if (isSelectedOverlay) {
            val overlayButton =
                IfOverlayButtonT(
                    player = player,
                    selectedSlot = message.selectedSub,
                    selectedObj = selectedObjType,
                    targetSlot = message.targetSub,
                    targetObj = targetObjType,
                    selectedComponent = selectedComponent,
                    targetComponent = targetComponent,
                )
            logger.debug { "[Overlay] IfButtonT: $message (overlayButton=$overlayButton)" }
            eventBus.publish(overlayButton)
            return
        }

        val modalButton =
            IfModalButtonT(
                selectedSlot = message.selectedSub,
                selectedObj = selectedObjType,
                targetSlot = message.targetSub,
                targetObj = targetObjType,
                selectedComponent = selectedComponent,
                targetComponent = targetComponent,
            )
        player.ifCloseInputDialog()
        if (player.isModalButtonProtected) {
            logger.debug { "[Modal][BLOCKED] IfButtonT: $message (modalButton=$modalButton)" }
            return
        }
        logger.debug { "[Modal] IfButtonT: $message (modalButton=$modalButton)" }
        protectedAccess.launchLenient(player) { eventBus.publish(this, modalButton) }
    }
}
