package gg.rsmod.plugins.core.protocol.packet.server

import gg.rsmod.game.message.ServerPacket

data class SmallVarpPacket(
    val id: Int,
    val value: Int
) : ServerPacket
