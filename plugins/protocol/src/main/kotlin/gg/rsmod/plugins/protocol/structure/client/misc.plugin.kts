package gg.rsmod.plugins.protocol.structure.client

import gg.rsmod.plugins.protocol.DesktopPacketStructure
import gg.rsmod.plugins.protocol.packet.client.NoTimeout
import gg.rsmod.plugins.protocol.packet.client.UnknownClientPacket
import gg.rsmod.plugins.protocol.packet.client.WindowStatus

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.client

packets.register<UnknownClientPacket> {
    opcode = 41
    length = -1
    suppress = true
}

packets.register<NoTimeout> {
    opcode = 0
    length = 0
    suppress = true
}

packets.register<WindowStatus> {
    opcode = 10
    length = 5
    suppress = true
}
