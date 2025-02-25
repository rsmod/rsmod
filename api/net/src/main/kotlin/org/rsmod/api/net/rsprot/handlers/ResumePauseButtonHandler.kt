package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.resumed.ResumePauseButton
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentTypeList
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

        val modal = player.ui.modals.getComponent(userInterface)
        if (modal != null) {
            player.ui.queueClose(modal)
            player.ui.queueResumeButton(componentType, message.sub)
            return
        }

        val overlay = player.ui.overlays.getComponent(userInterface)
        if (overlay != null) {
            player.ui.queueClose(overlay)
            player.ui.queueResumeButton(componentType, message.sub)
            return
        }
    }
}
