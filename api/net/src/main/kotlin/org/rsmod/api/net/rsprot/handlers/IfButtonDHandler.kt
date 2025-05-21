package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.buttons.IfButtonD
import org.rsmod.annotations.InternalApi
import org.rsmod.api.config.refs.objs
import org.rsmod.api.net.rsprot.player.InterfaceEvents
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.IfModalDrag
import org.rsmod.api.player.ui.IfOverlayDrag
import org.rsmod.api.player.ui.ifCloseInputDialog
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentTypeList
import org.rsmod.game.type.comp.UnpackedComponentType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.type.interf.isType
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.UserInterfaceMap

class IfButtonDHandler
@Inject
constructor(
    private val eventBus: EventBus,
    private val interfaceTypes: InterfaceTypeList,
    private val componentTypes: ComponentTypeList,
    private val protectedAccess: ProtectedAccessLauncher,
) : MessageHandler<IfButtonD> {
    private val logger = InlineLogger()

    @OptIn(InternalApi::class)
    override fun handle(player: Player, message: IfButtonD) {
        val selectedComponent = Component(message.selectedInterfaceId, message.selectedComponentId)
        val selectedComponentType = componentTypes[selectedComponent]
        val selectedInterface = interfaceTypes[selectedComponent]
        val targetComponent = Component(message.targetInterfaceId, message.targetComponentId)
        val targetComponentType = componentTypes[targetComponent]
        val targetInterface = interfaceTypes[targetComponent]
        val ui = player.ui

        val isSelectedOpenedModal = ui.containsModal(selectedInterface)
        val isSelectedOpened = isSelectedOpenedModal || ui.containsOverlay(selectedInterface)
        if (!isSelectedOpened) {
            logger.debug { "Selected interface is not open: message=$message, player=$player" }
            return
        }

        // No need to verify again if dragging objs on the same interface.
        val skipTargetVerification = selectedInterface.isType(targetInterface)
        if (!skipTargetVerification) {
            val isTargetOpenedModal = ui.containsModal(targetInterface)
            val targetOpened = isTargetOpenedModal || ui.containsOverlay(targetInterface)
            if (!targetOpened) {
                logger.debug { "Target interface is not open: message=$message, player=$player" }
                return
            }
        }

        val selectedSub = message.selectedSub
        val targetSub = message.targetSub

        val dragEnabled =
            isDragEnabled(ui, selectedComponentType, selectedSub, targetComponentType, targetSub)
        if (!dragEnabled) {
            return
        }

        // Client replaces empty obj ids with `6512`. To make life easier, we simply replace those
        // with null obj types as that's what associated scripts should treat them as.
        val selectedObjType = convertNullReplacement(message.selectedObj)
        val targetObjType = convertNullReplacement(message.targetObj)

        val isSelectedOverlay = !isSelectedOpenedModal
        if (isSelectedOverlay) {
            val overlayDrag =
                IfOverlayDrag(
                    player = player,
                    selectedSlot = selectedSub,
                    selectedObj = selectedObjType,
                    targetSlot = targetSub,
                    targetObj = targetObjType,
                    selectedComponent = selectedComponent,
                    targetComponent = targetComponent,
                )
            logger.debug { "[Overlay] IfButtonD: $message (overlayDrag=$overlayDrag)" }
            eventBus.publish(overlayDrag)
            return
        }

        val modalDrag =
            IfModalDrag(
                selectedSlot = selectedSub,
                selectedObj = selectedObjType,
                targetSlot = targetSub,
                targetObj = targetObjType,
                selectedComponent = selectedComponent,
                targetComponent = targetComponent,
            )
        player.ifCloseInputDialog()
        if (player.isModalButtonProtected) {
            logger.debug { "[Modal][BLOCKED] IfButtonD: $message (modalDrag=$modalDrag)" }
            return
        }
        logger.debug { "[Modal] IfButtonD: $message (modalDrag=$modalDrag)" }
        protectedAccess.launchLenient(player) { eventBus.publish(this, modalDrag) }
    }

    private fun isDragEnabled(
        ui: UserInterfaceMap,
        from: UnpackedComponentType,
        fromSlot: Int,
        target: UnpackedComponentType,
        targetSlot: Int,
    ): Boolean {
        val dragFromEnabled = InterfaceEvents.isEnabled(ui, from, fromSlot, IfEvent.DragTarget)
        if (!dragFromEnabled) {
            return false
        }
        val dragToEnabled = InterfaceEvents.isEnabled(ui, target, targetSlot, IfEvent.DragTarget)
        return dragToEnabled
    }

    private fun convertNullReplacement(type: Int?): Int? {
        return if (type == objs.null_item_placeholder.id) {
            null
        } else {
            type
        }
    }
}
