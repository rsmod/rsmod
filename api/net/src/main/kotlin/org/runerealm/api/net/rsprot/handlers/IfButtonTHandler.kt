package org.runerealm.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.buttons.IfButtonT
import org.runerealm.annotations.InternalApi
import org.runerealm.api.net.rsprot.player.InterfaceEvents
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.ui.IfModalButtonT
import org.runerealm.api.player.ui.IfOverlayButtonT
import org.runerealm.api.player.ui.ifCloseInputDialog
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.type.comp.ComponentTypeList
import org.runerealm.game.type.comp.UnpackedComponentType
import org.runerealm.game.type.interf.IfEvent
import org.runerealm.game.type.interf.InterfaceTypeList
import org.runerealm.game.type.interf.isType
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.ui.Component
import org.runerealm.game.ui.UserInterfaceMap

class IfButtonTHandler
@Inject
constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val interfaceTypes: InterfaceTypeList,
    private val componentTypes: ComponentTypeList,
    private val protectedAccess: ProtectedAccessLauncher,
) : MessageHandler<IfButtonT> {
    private val logger = InlineLogger()

    @OptIn(InternalApi::class)
    override fun handle(player: Player, message: IfButtonT) {
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

        val selectedSub = message.selectedSub
        val targetSub = message.targetSub

        val targetEnabled =
            isTargetEnabled(ui, selectedComponentType, selectedSub, targetComponentType, targetSub)
        if (!targetEnabled) {
            return
        }

        val selectedObjType = objTypes[message.selectedObj]
        val targetObjType = objTypes[message.targetObj]

        val isSelectedOverlay = !isSelectedOpenedModal
        if (isSelectedOverlay) {
            val overlayButton =
                IfOverlayButtonT(
                    player = player,
                    selectedSlot = selectedSub,
                    selectedObj = selectedObjType,
                    targetSlot = targetSub,
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
                selectedSlot = selectedSub,
                selectedObj = selectedObjType,
                targetSlot = targetSub,
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

    private fun isTargetEnabled(
        ui: UserInterfaceMap,
        from: UnpackedComponentType,
        fromComsub: Int,
        target: UnpackedComponentType,
        targetComsub: Int,
    ): Boolean {
        val targetFromEnabled = InterfaceEvents.isEnabled(ui, from, fromComsub, IfEvent.TgtCom)
        if (!targetFromEnabled) {
            return false
        }
        val targetToEnabled = InterfaceEvents.isEnabled(ui, target, targetComsub, IfEvent.Target)
        return targetToEnabled
    }
}
