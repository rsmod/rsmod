package gg.rsmod.plugins.api.protocol.packet.server

import gg.rsmod.game.message.ServerPacket

data class SmallVarpPacket(
    val id: Int,
    val value: Int
) : ServerPacket
