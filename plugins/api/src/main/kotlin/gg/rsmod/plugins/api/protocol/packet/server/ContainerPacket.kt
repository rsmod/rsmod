package gg.rsmod.plugins.api.protocol.packet.server

import gg.rsmod.game.message.ServerPacket
import gg.rsmod.game.model.item.Item

data class UpdateInvFull(
    val key: Int = -1,
    val component: Int = 0,
    val items: List<Item?>
) : ServerPacket
