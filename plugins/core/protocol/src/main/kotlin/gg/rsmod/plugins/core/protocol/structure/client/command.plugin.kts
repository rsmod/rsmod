package gg.rsmod.plugins.core.protocol.structure.client

import gg.rsmod.game.message.PacketLength
import gg.rsmod.plugins.core.protocol.packet.client.ClientCheat
import gg.rsmod.plugins.core.protocol.packet.client.ClientCheatHandler
import gg.rsmod.plugins.core.protocol.structure.DesktopPacketStructure
import io.guthix.buffer.readStringCP1252

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.client

packets.register<ClientCheat> {
    opcode = 40
    length = -1
    handler = ClientCheatHandler::class
    read {
        val input = readStringCP1252()
        ClientCheat(input)
    }
}