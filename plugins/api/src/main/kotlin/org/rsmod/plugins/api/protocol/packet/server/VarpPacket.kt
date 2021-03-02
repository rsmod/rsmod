package org.rsmod.plugins.api.protocol.packet.server

import org.rsmod.game.message.ServerPacket

data class VarpSmall(
    val id: Int,
    val value: Int
) : ServerPacket

data class VarpLarge(
    val id: Int,
    val value: Int
) : ServerPacket
