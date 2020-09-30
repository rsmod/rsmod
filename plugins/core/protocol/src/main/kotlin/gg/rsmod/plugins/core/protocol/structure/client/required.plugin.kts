package gg.rsmod.plugins.core.protocol.structure.client

import gg.rsmod.plugins.core.protocol.packet.client.NoTimeout
import gg.rsmod.plugins.core.protocol.packet.client.UnknownClientPacket
import gg.rsmod.plugins.core.protocol.packet.client.WindowStatus
import gg.rsmod.plugins.core.protocol.structure.DesktopPacketStructure

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.client

packets.register<UnknownClientPacket> {
    opcode = 41
    length = -1
}

packets.register<NoTimeout> {
    opcode = 0
    length = 0
}

packets.register<WindowStatus> {
    opcode = 10
    length = 5
}
