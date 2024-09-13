package org.rsmod.api.net.rsprot.handlers

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.resumed.ResumePauseButton
import org.rsmod.api.player.input.ResumePauseButtonInput
import org.rsmod.api.player.ui.ifCloseSub
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentTypeList
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.ui.Component

class ResumePauseButtonHandler
@Inject
constructor(
    private val eventBus: EventBus,
    private val interfaceTypes: InterfaceTypeList,
    private val componentTypes: ComponentTypeList,
) : MessageHandler<ResumePauseButton> {
    private val ResumePauseButton.asComponent: Component
        get() = Component(interfaceId, componentId)

    override fun handle(player: Player, message: ResumePauseButton) {
        val componentType = componentTypes[message.asComponent]
        val interfaceType = interfaceTypes[message.asComponent]
        val input = ResumePauseButtonInput(componentType, message.sub)
        if (player.ui.containsModal(interfaceType)) {
            player.ifCloseSub(interfaceType, eventBus)
            player.resumeActiveCoroutine(input)
        }
    }
}
