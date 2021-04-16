package org.rsmod.plugins.api.protocol.packet.server

import org.rsmod.game.message.ServerPacket

data class IfOpenTop(val interfaceId: Int) : ServerPacket

data class IfOpenSub(
    val interfaceId: Int,
    val targetComponent: Int,
    val clickMode: Int
) : ServerPacket

data class IfCloseSub(val component: Int) : ServerPacket

data class IfSetText(val component: Int, val text: String) : ServerPacket

data class IfSetEvents(
    val component: Int,
    val dynamic: IntRange,
    val events: Int
) : ServerPacket

class RunClientScript(
    val id: Int,
    vararg val args: Any
) : ServerPacket
