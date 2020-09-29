package gg.rsmod.game.message

import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.mob.Player

interface ServerPacketListener {

    fun write(packet: ServerPacket)

    fun flush()
}

interface ClientPacketHandler<T : ClientPacket> {

    fun handle(client: Client, player: Player, packet: T)
}
