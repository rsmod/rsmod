package gg.rsmod.game.action

import gg.rsmod.game.message.ClientPacket
import gg.rsmod.game.message.ClientPacketHandler

interface Action

interface ActionType

data class ActionMessage<T : ClientPacket>(
    val packet: T,
    val handler: ClientPacketHandler<ClientPacket>
)
