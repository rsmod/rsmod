package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.resumed.ResumePauseButton
import org.rsmod.api.net.rsprot.player.InterfaceEvents
import org.rsmod.api.player.input.ResumePauseButtonInput
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentTypeList
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.UserInterface

class ResumePauseButtonHandler
@Inject
constructor(
    private val interfaceTypes: InterfaceTypeList,
    private val componentTypes: ComponentTypeList,
) : MessageHandler<ResumePauseButton> {
    private val ResumePauseButton.asComponent: Component
        get() = Component(interfaceId, componentId)

    override fun handle(player: Player, message: ResumePauseButton) {
        val componentType = componentTypes[message.asComponent]
        val interfaceType = interfaceTypes[message.asComponent]
        val userInterface = UserInterface(interfaceType)

        val pauseEnabled =
            InterfaceEvents.isEnabled(player.ui, componentType, message.sub, IfEvent.PauseButton)
        if (!pauseEnabled) {
            return
        }

        val input = ResumePauseButtonInput(componentType, message.sub)
        val modal = player.ui.modals.getComponent(userInterface)
        if (modal != null) {
            player.ui.queueClose(modal)
            player.resumeActiveCoroutine(input)
            return
        }

        val overlay = player.ui.overlays.getComponent(userInterface)
        if (overlay != null) {
            player.ui.queueClose(overlay)
            player.resumeActiveCoroutine(input)
            return
        }
    }
}
