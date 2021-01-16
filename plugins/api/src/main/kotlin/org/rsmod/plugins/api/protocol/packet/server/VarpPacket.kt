package org.rsmod.plugins.api.protocol.packet.server

import org.rsmod.game.message.ServerPacket

data class SmallVarpPacket(
    val id: Int,
    val value: Int
) : ServerPacket

data class LargeVarpPacket(
    val id: Int,
    val value: Int
) : ServerPacket
