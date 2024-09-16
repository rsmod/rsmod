package org.rsmod.api.net.rsprot.handlers

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.buttons.If3Button
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.IfModalButton
import org.rsmod.api.player.ui.IfOverlayButton
import org.rsmod.api.player.ui.IfTopLevelButton
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentTypeList
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.interf.InterfaceTypeList
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.ui.Component

class If3ButtonHandler
@Inject
constructor(
    private val eventBus: EventBus,
    private val interfaceTypes: InterfaceTypeList,
    private val componentTypes: ComponentTypeList,
    private val objTypes: ObjTypeList,
    private val protectedAccess: ProtectedAccessLauncher,
) : MessageHandler<If3Button> {
    private val logger = InlineLogger()

    private val If3Button.asComponent: Component
        get() = Component(interfaceId, componentId)

    private val If3Button.buttonOp: IfButtonOp
        get() =
            when (op) {
                1 -> IfButtonOp.Op1
                2 -> IfButtonOp.Op2
                3 -> IfButtonOp.Op3
                4 -> IfButtonOp.Op4
                5 -> IfButtonOp.Op5
                6 -> IfButtonOp.Op6
                7 -> IfButtonOp.Op7
                8 -> IfButtonOp.Op8
                9 -> IfButtonOp.Op9
                10 -> IfButtonOp.Op10
                else -> throw NotImplementedError("Unhandled If3Button op: $this")
            }

    @OptIn(ProtectedAccessLauncher.InternalApi::class)
    override fun handle(player: Player, message: If3Button) {
        val componentType = componentTypes[message.asComponent]
        val interfaceType = interfaceTypes[message.asComponent]
        val objType = objTypes[message.obj]
        if (player.ui.containsTopLevel(interfaceType)) {
            val event =
                IfTopLevelButton(player, componentType, message.sub, objType, message.buttonOp)
            eventBus.publish(event)
            logger.debug { "[Toplevel] If3Button: $message" }
            return
        }
        if (player.ui.containsOverlay(interfaceType)) {
            val event =
                IfOverlayButton(player, componentType, message.sub, objType, message.buttonOp)
            eventBus.publish(event)
            logger.debug { "[Overlay] If3Button: $message" }
            return
        }
        if (player.ui.containsModal(interfaceType)) {
            if (!player.isModalButtonProtected) {
                val event = IfModalButton(componentType, message.sub, objType, message.buttonOp)
                protectedAccess.launchLenient(player) { eventBus.publish(this, event) }
                logger.debug { "[Modal] If3Button: $message" }
            } else {
                logger.debug { "[Modal][BLOCKED] If3Button: $message" }
            }
            return
        }
    }
}
