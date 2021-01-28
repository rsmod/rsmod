package org.rsmod.plugins.api.protocol.packet.server

import org.rsmod.game.message.ServerPacket

inline class IfOpenTop(
    val interfaceId: Int
) : ServerPacket

data class IfOpenSub(
    val interfaceId: Int,
    val targetComponent: Int,
    val clickMode: Int
) : ServerPacket

inline class IfCloseSub(
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
