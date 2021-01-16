package org.rsmod.game.message

import org.rsmod.game.model.client.Client
import org.rsmod.game.model.mob.Player

interface ServerPacketListener {

    fun write(packet: ServerPacket)

    fun flush()

    fun close()
}

interface ClientPacketHandler<T : ClientPacket> {

    fun handle(client: Client, player: Player, packet: T)
}
