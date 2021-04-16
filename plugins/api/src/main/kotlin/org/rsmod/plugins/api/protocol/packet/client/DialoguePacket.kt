package org.rsmod.plugins.api.protocol.packet.client

import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.item.type.ItemTypeList
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.event.ItemSearchInput
import javax.inject.Inject

data class ResumePObjDialog(val item: Int) : ClientPacket

class ResumePObjDialogHandler @Inject constructor(
    private val types: ItemTypeList
) : ClientPacketHandler<ResumePObjDialog> {

    override fun handle(client: Client, player: Player, packet: ResumePObjDialog) {
        val type = types.getOrNull(packet.item) ?: return
        val event = ItemSearchInput(type)
        player.submitEvent(event)
    }
}
