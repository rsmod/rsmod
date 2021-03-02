package org.rsmod.plugins.api.protocol.packet.server

import org.rsmod.game.message.ServerPacket

data class MessageGame(
    val type: Int,
    val text: String,
    val username: String?
) : ServerPacket
