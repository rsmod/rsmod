package org.rsmod.plugins.api.protocol.packet.server

import org.rsmod.game.message.ServerPacket
import org.rsmod.game.model.item.Item

data class UpdateInvFull(
    val key: Int = -1,
    val component: Int = -1,
    val items: List<Item?>
) : ServerPacket

data class UpdateInvPartial(
    val key: Int = -1,
    val component: Int = -1,
    val updated: Map<Int, Item?>
) : ServerPacket
