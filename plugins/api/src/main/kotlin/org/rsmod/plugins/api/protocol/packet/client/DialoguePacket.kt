package org.rsmod.plugins.api.protocol.packet.client

import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.item.type.ItemTypeList
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.ui.Component
import org.rsmod.plugins.api.event.ContinueDialogue
import org.rsmod.plugins.api.event.IntChatInput
import org.rsmod.plugins.api.event.ItemSearchInput
import javax.inject.Inject

data class ResumePObjDialog(val item: Int) : ClientPacket
data class ResumePCountDialog(val amount: Int) : ClientPacket
data class ResumePauseButton(val component: Int, val slot: Int) : ClientPacket

class ResumePObjDialogHandler @Inject constructor(
    private val types: ItemTypeList
) : ClientPacketHandler<ResumePObjDialog> {

    override fun handle(client: Client, player: Player, packet: ResumePObjDialog) {
        val type = types.getOrNull(packet.item) ?: return
        val event = ItemSearchInput(type)
        player.submitEvent(event)
    }
}

class ResumePCountDialogHandler : ClientPacketHandler<ResumePCountDialog> {

    override fun handle(client: Client, player: Player, packet: ResumePCountDialog) {
        val event = IntChatInput(packet.amount)
        player.submitEvent(event)
    }
}

class ResumePauseButtonHandler : ClientPacketHandler<ResumePauseButton> {

    override fun handle(client: Client, player: Player, packet: ResumePauseButton) {
        val component = Component(packet.component)
        val event = ContinueDialogue(component, packet.slot)
        player.submitEvent(event)
    }
}
