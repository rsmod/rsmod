package gg.rsmod.plugins.core.protocol.packet.server

import gg.rsmod.game.message.ServerPacket

data class IfOpenTop(
    val interfaceId: Int
) : ServerPacket

data class IfOpenSub(
    val interfaceId: Int,
    val targetComponent: Int,
    val type: Int
) : ServerPacket

class RunClientScript(
    val id: Int,
    vararg val args: Any
) : ServerPacket
