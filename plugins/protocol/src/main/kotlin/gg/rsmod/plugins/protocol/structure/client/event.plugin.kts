package gg.rsmod.plugins.protocol.structure.client

import gg.rsmod.plugins.protocol.packet.client.EventAppletFocus
import gg.rsmod.plugins.protocol.packet.client.EventMouseClick
import gg.rsmod.plugins.protocol.packet.client.EventMouseMove
import gg.rsmod.plugins.protocol.structure.DesktopPacketStructure

val desktopPackets: DesktopPacketStructure by inject()
val packets = desktopPackets.client

packets.register<EventMouseMove> {
    opcode = 23
    length = -1
}

packets.register<EventMouseClick> {
    opcode = 66
    length = 6
}

packets.register<EventAppletFocus> {
    opcode = 26
    length = 1
}
