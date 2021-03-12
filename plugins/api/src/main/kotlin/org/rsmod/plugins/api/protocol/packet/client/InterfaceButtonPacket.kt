package org.rsmod.plugins.api.protocol.packet.client

import javax.inject.Inject
import org.rsmod.game.action.ActionBus
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.ui.Component
import org.rsmod.plugins.api.protocol.packet.ButtonClick

data class IfButton(
    val clickType: Int,
    val component: Int,
    val child: Int,
    val item: Int
) : ClientPacket {

    companion object {

        /**
         * The offset, relative to zero, added to [clickType].
         *
         * For consistency with other clicking packets, button
         * "type" identifier begins here. For example, IF_BUTTON1
         * will have a [clickType] of {1}.
         */
        const val TYPE_INDEX_OFFSET = 1
    }
}

class IfButtonHandler @Inject constructor(
    private val actions: ActionBus
) : ClientPacketHandler<IfButton> {

    override fun handle(client: Client, player: Player, packet: IfButton) {
        val (clickType, componentValue, child, item) = packet
        val component = Component(componentValue)
        val action = ButtonClick(player, clickType, component, child, item)
        val published = actions.publish(action, component.packed)
        if (!published) {
            player.warn { "Unhandled button action: $action" }
        }
    }
}
