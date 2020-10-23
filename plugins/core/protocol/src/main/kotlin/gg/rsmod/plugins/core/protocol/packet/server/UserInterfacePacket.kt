package gg.rsmod.plugins.core.protocol.packet.server

import gg.rsmod.game.message.ServerPacket

data class IfOpenTop(
    val interfaceId: Int
) : ServerPacket

data class IfOpenSub(
    val interfaceId: Int,
    val targetComponent: Int,
    val clickMode: Int
) : ServerPacket

data class IfCloseSub(
    val component: Int
) : ServerPacket

data class IfSetEvents(
    val component: Int,
    val dynamic: IntRange,
    val event: Int
) : ServerPacket

class RunClientScript(
    val id: Int,
    vararg val args: Any
) : ServerPacket
